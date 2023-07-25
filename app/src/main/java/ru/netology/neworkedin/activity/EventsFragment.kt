package ru.netology.neworkedin.activity

import android.app.AlertDialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.*
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.neworkedin.R
import ru.netology.neworkedin.adapters.*
import ru.netology.neworkedin.auth.AppAuth
import ru.netology.neworkedin.utils.Companion.Companion.eventId
import ru.netology.neworkedin.utils.Companion.Companion.eventRequestType
import ru.netology.neworkedin.utils.Companion.Companion.textArg
import ru.netology.neworkedin.utils.Companion.Companion.userId
import ru.netology.neworkedin.utils.FloatValue.currentFragment
import ru.netology.neworkedin.databinding.FragmentEventsBinding
import ru.netology.neworkedin.dataclass.*
import ru.netology.neworkedin.viewmodel.*
import javax.inject.Inject

/** ОТВЕЧАЕТ ЗА СОБЫТИЯ */

@AndroidEntryPoint
class EventsFragment : Fragment() {

    val viewModel: EventViewModel by activityViewModels()
    val authViewModel: AuthViewModel by viewModels()
    private lateinit var binding: FragmentEventsBinding
    private lateinit var adapter: EventAdapter

    @Inject
    lateinit var appAuth: AppAuth

    val mediaPlayer = MediaPlayer()

    private val interactionListener = object : OnInteractionListenerEvent {

        override fun onTapAvatar(event: Event) {
            findNavController().navigate(
                R.id.action_eventsFragment_to_profileFragment,
                Bundle().apply {
                    userId = event.authorId
                }
            )
        }

        override fun onLike(event: Event) {
            if (authViewModel.authenticated) {
                viewModel.likeById(event)
            } else {
                AlertDialog.Builder(context)
                    .setMessage(R.string.action_not_allowed)
                    .setPositiveButton(R.string.sign_up) { _, _ ->
                        findNavController().navigate(
                            R.id.action_eventsFragment_to_authFragment,
                            Bundle().apply {
                                textArg = getString(R.string.sign_up)
                            }
                        )
                    }
                    .setNeutralButton(R.string.sign_in) { _, _ ->
                        findNavController().navigate(
                            R.id.action_eventsFragment_to_authFragment,
                            Bundle().apply {
                                textArg = getString(R.string.sign_in)
                            }
                        )
                    }
                    .setNegativeButton(R.string.no, null)
                    .setCancelable(true)
                    .create()
                    .show()
            }
        }

        override fun onShare(event: Event) {
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, event.content)
            }

            val shareIntent =
                Intent.createChooser(intent, getString(R.string.chooser_share_post))
            startActivity(shareIntent)
        }

        override fun onRemove(event: Event) {
            viewModel.removeById(event.id)
        }

        override fun onEdit(event: Event) {
            viewModel.edit(event)
            findNavController().navigate(R.id.action_eventsFragment_to_newEventFragment)
        }

        override fun onPlayPost(event: Event, videoView: VideoView?) {
            if (event.attachment?.type == AttachmentType.VIDEO) {
                videoView?.isVisible = true
                val uri = Uri.parse(event.attachment.url)

                videoView?.apply {
                    setMediaController(MediaController(requireContext()))
                    setVideoURI(uri)
                    setOnPreparedListener {
                        videoView.layoutParams?.height =
                            (resources.displayMetrics.widthPixels * (it.videoHeight.toDouble() / it.videoWidth)).toInt()
                        start()
                    }

                    setOnCompletionListener {

                        if (videoView.layoutParams?.width != null) {
                            videoView.layoutParams?.width = resources.displayMetrics.widthPixels
                            videoView.layoutParams?.height =
                                (videoView.layoutParams?.width!! * 0.5625).toInt()
                        }
                        stopPlayback()

                    }

                }
            }
            if (event.attachment?.type == AttachmentType.AUDIO) {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                } else {
                    mediaPlayer.reset()
                    mediaPlayer.setDataSource(event.attachment.url)
                    mediaPlayer.prepare()
                    mediaPlayer.start()
                }
            }
        }

        override fun onLink(event: Event) {
            val intent = if (event.link?.contains("https://") == true || event.link?.contains("http://") == true) {
                Intent(Intent.ACTION_VIEW, Uri.parse(event.link))
            } else {
                Intent(Intent.ACTION_VIEW, Uri.parse("http://${event.link}"))
            }
            startActivity(intent)
        }

        override fun onPreviewAttachment(event: Event) {
            findNavController().navigate(
                R.id.action_eventsFragment_to_viewImageAttach,
                Bundle().apply {
                    textArg = event.attachment?.url
                })
        }

        override fun onSpeakersAction(event: Event) {
            if (event.speakerIds.isNotEmpty()) {
                findNavController().navigate(
                    R.id.action_eventsFragment_to_bottomSheetFragment,
                    Bundle().apply {
                        eventId = event.id
                        eventRequestType = "speakers"
                    }
                )
            } else {
                Toast.makeText(requireContext(), R.string.not_value_event, Toast.LENGTH_SHORT)
                    .show()
            }

        }

        override fun onPartyAction(event: Event) {
            if (event.participantsIds.isNotEmpty()) {
                findNavController().navigate(
                    R.id.action_eventsFragment_to_bottomSheetFragment,
                    Bundle().apply {
                        eventId = event.id
                        eventRequestType = "party"
                    }
                )
            } else {
                Toast.makeText(requireContext(), R.string.not_value_event, Toast.LENGTH_SHORT)
                    .show()
            }
        }

        override fun onJoinAction(event: Event) {
            viewModel.joinById(event)
        }
    }

    //создание view
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentEventsBinding.inflate(layoutInflater)
        adapter = EventAdapter(interactionListener)
        binding.list.adapter = adapter

        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest {
                adapter.submitList(it)
            }
        }

        var menuProvider: MenuProvider? = null

        authViewModel.data.observe(viewLifecycleOwner) {
            menuProvider?.let(requireActivity()::removeMenuProvider)
            requireActivity().addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_main, menu)

                    menu.setGroupVisible(R.id.unauthenticated, !authViewModel.authenticated)
                    menu.setGroupVisible(R.id.authenticated, authViewModel.authenticated)

                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return when (menuItem.itemId) {
                        R.id.signin -> {
                            findNavController().navigate(
                                R.id.action_feedFragment_to_authFragment,
                                Bundle().apply {
                                    textArg = getString(R.string.sign_in)
                                }
                            )
                            true
                        }
                        R.id.signup -> {
                            findNavController().navigate(
                                R.id.action_feedFragment_to_authFragment,
                                Bundle().apply {
                                    textArg = getString(R.string.sign_up)
                                }
                            )
                            true
                        }
                        R.id.signout -> {
                            AlertDialog.Builder(requireActivity())
                                .setTitle(R.string.are_you_suare)
                                .setPositiveButton(R.string.yes) { _, _ ->
                                    appAuth.removeAuth()
                                }

                                .setCancelable(true)
                                .setNegativeButton(R.string.no, null)
                                .create()
                                .show()
                            true
                        }
                        else -> false
                    }
                }
            }.apply {
                menuProvider = this
            }, viewLifecycleOwner)
        }

        binding.mainNavView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_posts -> {
                    findNavController().navigate(R.id.action_eventsFragment_to_feedFragment)
                    true
                }

                R.id.navigation_events -> {
                    true
                }

                R.id.navigation_users -> {
                    findNavController().navigate(R.id.action_eventsFragment_to_usersFragment)
                    true
                }

                R.id.navigation_profile -> {
                    findNavController().navigate(R.id.action_eventsFragment_to_profileFragment)
                    true
                }

                else -> false
            }
        }
        binding.mainNavView.selectedItemId = R.id.navigation_events
        binding.fab.setOnClickListener {
            if (authViewModel.authenticated) {
                findNavController().navigate(R.id.action_eventsFragment_to_newEventFragment)
            } else {
                AlertDialog.Builder(context)
                    .setMessage(R.string.action_not_allowed)
                    .setPositiveButton(R.string.sign_up) { _, _ ->
                        findNavController().navigate(
                            R.id.action_eventsFragment_to_authFragment,
                            Bundle().apply {
                                textArg = getString(R.string.sign_up)
                            }
                        )
                    }
                    .setNeutralButton(R.string.sign_in) { _, _ ->
                        findNavController().navigate(
                            R.id.action_eventsFragment_to_authFragment,
                            Bundle().apply {
                                textArg = getString(R.string.sign_in)
                            }
                        )
                    }
                    .setNegativeButton(R.string.no, null)
                    .setCancelable(true)
                    .create()
                    .show()
            }
        }

        binding.swipe.setOnRefreshListener {
            viewModel.loadEvents()
            binding.swipe.isRefreshing = false
        }

        return binding.root
    }

    override fun onDestroyView() {
        mediaPlayer.release()
        super.onDestroyView()
    }

    override fun onResume() {
        currentFragment = javaClass.simpleName
        super.onResume()
    }
}