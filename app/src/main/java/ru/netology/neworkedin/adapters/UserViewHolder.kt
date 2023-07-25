package ru.netology.neworkedin.adapters

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.neworkedin.R
import ru.netology.neworkedin.databinding.FragmentCardUsersBinding
import ru.netology.neworkedin.dataclass.User

class UserViewHolder(
    private val binding: FragmentCardUsersBinding,
    private val onInteractionListener: OnInteractionListenerUsers
) : RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("SetTextI18n")
    fun renderingPostStructure(user: User) {
        with(binding) {
            idUser.text = "[id: ${user.id}]"
            loginUser.text = user.login
            nameUser.text = "(${user.name})"
            Glide.with(avatar)
                .load(user.avatar)
                .placeholder(R.drawable.ic_image_not_supported_24)
                .error(R.drawable.ic_not_avatars_24)
                .circleCrop()
                .timeout(10_000)
                .into(avatar)
            userListeners(user)
        }
    }

    private fun userListeners(user: User) {
        binding.userCard.setOnClickListener {
            onInteractionListener.onTap(user)
        }
    }
}