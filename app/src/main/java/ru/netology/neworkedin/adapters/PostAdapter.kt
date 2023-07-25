package ru.netology.neworkedin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import ru.netology.neworkedin.databinding.FragmentCardPostBinding
import ru.netology.neworkedin.dataclass.Post

/** Posy... ОТВЕЧАЕТ ЗА РЕНДЕРИНГ ПОСТОВ, ИХ ВНЕШНИЙ ВИД, "СЛУШАЕТ" НАЖАТИЯ */

class PostAdapter(
    private val onInteractionListener: OnInteractionListener
) : ListAdapter<Post,PostViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding =
            FragmentCardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.renderingPostStructure(post)
    }

    override fun onViewRecycled(holder: PostViewHolder) {
        super.onViewRecycled(holder)
        holder.binding.videoAttachment.stopPlayback()
        holder.binding.videoAttachment.setVideoURI(null)
    }
}