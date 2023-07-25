package ru.netology.neworkedin.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.neworkedin.R
import ru.netology.neworkedin.adapters.*
import ru.netology.neworkedin.utils.Companion.Companion.eventId
import ru.netology.neworkedin.utils.Companion.Companion.eventRequestType
import ru.netology.neworkedin.utils.Companion.Companion.userId
import ru.netology.neworkedin.databinding.FragmentBottomSheetBinding
import ru.netology.neworkedin.dataclass.User
import ru.netology.neworkedin.viewmodel.*

/** СПИСОК ПОСТОВ */

@AndroidEntryPoint
class BottomSheetFragment : BottomSheetDialogFragment() {

    private val usersViewModel: UserViewModel by activityViewModels()
    val viewModel: EventViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentBottomSheetBinding.inflate(inflater, container, false)
        val adapter = UsersAdapter(object : OnInteractionListenerUsers {
            override fun onTap(user: User) {
                findNavController().navigate(
                    R.id.action_bottomSheetFragment_to_profileFragment,
                    Bundle().apply { userId = user.id }
                )
            }
        })

        var filteredList: List<Long> = emptyList()

        binding.list.adapter = adapter

        //уходим в многопоточку
        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest {
                when (arguments?.eventRequestType) {
                    "speakers" -> filteredList = it.find { event ->
                        event.id == arguments?.eventId
                    }?.speakerIds ?: emptyList()
                    "party" -> filteredList = it.find { event ->
                        event.id == arguments?.eventId
                    }?.participantsIds ?: emptyList()
                    else -> emptyList<Long>()
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            usersViewModel.dataUsersList.collectLatest {
                adapter.submitList(it.filter { user ->
                    filteredList.contains(user.id)
                })
            }
        }
        return binding.root
    }
}