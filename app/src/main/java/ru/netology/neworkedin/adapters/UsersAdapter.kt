package ru.netology.neworkedin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import ru.netology.neworkedin.databinding.FragmentCardUsersBinding
import ru.netology.neworkedin.dataclass.User

/** User... ОТВЕЧАЕТ ЗА РЕНДЕРИНГ ЮЗЕРОВ, ИХ ВНЕШНИЙ ВИД, "СЛУШАЕТ" НАЖАТИЯ */

class UsersAdapter(
    private val onInteractionListener: OnInteractionListenerUsers = object : OnInteractionListenerUsers {}
) : ListAdapter<User, UserViewHolder>(UserDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding =
            FragmentCardUsersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position)
        holder.renderingPostStructure(user)
    }
}