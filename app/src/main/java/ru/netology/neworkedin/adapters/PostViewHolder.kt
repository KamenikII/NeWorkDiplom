package ru.netology.neworkedin.adapters

import android.content.res.Resources
import android.view.View
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.netology.neworkedin.R
import ru.netology.neworkedin.databinding.FragmentCardPostBinding
import ru.netology.neworkedin.dataclass.AttachmentType
import ru.netology.neworkedin.dataclass.Post
import ru.netology.neworkedin.utils.FloatValue
import ru.netology.neworkedin.utils.Numbers
import kotlin.coroutines.EmptyCoroutineContext

class PostViewHolder(
    val binding: FragmentCardPostBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun renderingPostStructure(post: Post) {
        with(binding) {
            title.text = post.author
            datePublished.text = FloatValue.convertDatePublished(post.published)
            content.text = post.content
            like.text = Numbers.translateNumber(post.likeOwnerIds?.size ?: 0)
            like.isChecked = post.likedByMe
            share.isChecked = post.sharedByMe
            mentions.text = Numbers.translateNumber(post.mentionIds?.size ?: 0)
            mentions.isCheckable = true
            mentions.isClickable = false
            mentions.isChecked = post.mentionedMe
            links.isVisible = (post.link != null)

            if (post.link != null) {
                links.text = post.link
            }

            Glide.with(avatar)
                .load(post.authorAvatar)
                .placeholder(R.drawable.ic_image_not_supported_24)
                .error(R.drawable.ic_not_avatars_24)
                .circleCrop()
                .timeout(10_000)
                .into(avatar)

            moreVert.visibility = if (post.ownedByMe) View.VISIBLE else View.INVISIBLE

            if (post.attachment != null) {
                attachmentContent.isVisible = true
                if(post.attachment.type == AttachmentType.IMAGE) {
                    Glide.with(imageAttachment)
                        .load(post.attachment.url)
                        .placeholder(R.drawable.ic_image_not_supported_24)
                        .apply(
                            RequestOptions.overrideOf(
                                Resources.getSystem().displayMetrics.widthPixels
                            )
                        )
                        .timeout(10_000)
                        .into(imageAttachment)
                }

                videoAttachment.isVisible = (post.attachment.type == AttachmentType.VIDEO)
                playButtonPost.isVisible = (post.attachment.type == AttachmentType.VIDEO)
                playButtonPostAudio.isVisible = (post.attachment.type == AttachmentType.AUDIO)
                imageAttachment.isVisible = (post.attachment.type == AttachmentType.IMAGE)
                descriptionAttachment.isVisible = (post.attachment.type == AttachmentType.AUDIO)
            } else {
                attachmentContent.visibility = View.GONE
            }

            postListeners(post)
        }
    }

    private fun postListeners(post: Post) {
        with(binding) {
            like.setOnClickListener {
                like.isChecked = !like.isChecked //Инвертируем нажатие
                onInteractionListener.onLike(post)
            }

            share.setOnClickListener {
                onInteractionListener.onShare(post)
            }

            playButtonPostAudio.setOnClickListener {
                CoroutineScope(EmptyCoroutineContext).launch {
                    onInteractionListener.onPlayPost(post)
                }
            }

            playButtonPost.setOnClickListener {
                onInteractionListener.onPlayPost(post, binding.videoAttachment)
            }

            imageAttachment.setOnClickListener {
                onInteractionListener.onPreviewAttachment(post)
            }

            links.setOnClickListener {
                onInteractionListener.onLink(post)
            }

            avatar.setOnClickListener {
                onInteractionListener.onTapAvatar(post)
            }

            moreVert.setOnClickListener {
                val popupMenu = PopupMenu(it.context, it)

                popupMenu.apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                moreVert.isChecked = false
                                onInteractionListener.onRemove(post)
                                true
                            }

                            R.id.edit -> {
                                moreVert.isChecked = false
                                onInteractionListener.onEdit(post)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()

                popupMenu.setOnDismissListener {
                    moreVert.isChecked = false
                }
            }
        }
    }
}