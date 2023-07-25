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
import ru.netology.neworkedin.databinding.FragmentCardEventBinding
import ru.netology.neworkedin.dataclass.AttachmentType
import ru.netology.neworkedin.dataclass.Event
import ru.netology.neworkedin.utils.FloatValue
import ru.netology.neworkedin.utils.Numbers
import kotlin.coroutines.EmptyCoroutineContext

class EventViewHolder(
    val binding: FragmentCardEventBinding,
    private val onInteractionListener: OnInteractionListenerEvent,
) : RecyclerView.ViewHolder(binding.root) {


    fun renderingPostStructure(event: Event) {
        with(binding) {
            title.text = event.author
            datePublished.text = FloatValue.convertDatePublished(event.published)
            content.text = event.content
            like.text = Numbers.translateNumber(event.likeOwnerIds.size)
            like.isChecked = event.likedByMe
            eventDateValue.text = FloatValue.convertDatePublished(event.datetime).dropLast(3)
            eventFormatValue.text = event.type.name
            joinButton.isChecked = event.participatedByMe

            joinButton.text = if (joinButton.isChecked) {
                binding.root.context.getString(R.string.un_join)
            } else {
                binding.root.context.getString(R.string.join)
            }

            links.isVisible = (event.link != null)

            if (event.link != null) {
                links.text = event.link
            }

            Glide.with(avatar)
                .load(event.authorAvatar)
                .placeholder(R.drawable.ic_image_not_supported_24)
                .error(R.drawable.ic_not_avatars_24)
                .circleCrop()
                .timeout(10_000)
                .into(avatar)

            moreVert.visibility = if (event.ownedByMe) View.VISIBLE else View.INVISIBLE

            if (event.attachment != null) {

                attachmentContent.isVisible = true

                if(event.attachment.type == AttachmentType.IMAGE) {
                    Glide.with(imageAttachment)
                        .load(event.attachment.url)
                        .placeholder(R.drawable.ic_image_not_supported_24)
                        .apply(
                            RequestOptions.overrideOf(
                                Resources.getSystem().displayMetrics.widthPixels
                            )
                        )
                        .timeout(10_000)
                        .into(imageAttachment)
                }

                videoAttachment.isVisible = (event.attachment.type == AttachmentType.VIDEO)
                playButtonPost.isVisible = (event.attachment.type == AttachmentType.VIDEO)
                playButtonPostAudio.isVisible = (event.attachment.type == AttachmentType.AUDIO)
                imageAttachment.isVisible = (event.attachment.type == AttachmentType.IMAGE)
                descriptionAttachment.isVisible = (event.attachment.type == AttachmentType.AUDIO)
            } else {
                attachmentContent.visibility = View.GONE
            }

            postListeners(event)
        }
    }

    private fun postListeners(event: Event) {
        with(binding) {
            like.setOnClickListener {
                like.isChecked = !like.isChecked //Инвертируем нажатие
                onInteractionListener.onLike(event)
            }

            share.setOnClickListener {
                onInteractionListener.onShare(event)
            }

            playButtonPostAudio.setOnClickListener {
                CoroutineScope(EmptyCoroutineContext).launch {
                    onInteractionListener.onPlayPost(event)
                }
            }

            playButtonPost.setOnClickListener {
                onInteractionListener.onPlayPost(event, binding.videoAttachment)
            }

            imageAttachment.setOnClickListener {
                onInteractionListener.onPreviewAttachment(event)
            }

            links.setOnClickListener {
                onInteractionListener.onLink(event)
            }

            partyButton.setOnClickListener{
                onInteractionListener.onPartyAction(event)
            }

            joinButton.setOnClickListener{
                onInteractionListener.onJoinAction(event)
            }

            speakersButton.setOnClickListener{
                onInteractionListener.onSpeakersAction(event)
            }

            avatar.setOnClickListener {
                onInteractionListener.onTapAvatar(event)
            }

            moreVert.setOnClickListener {
                val popupMenu = PopupMenu(it.context, it)
                popupMenu.apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                moreVert.isChecked = false
                                onInteractionListener.onRemove(event)
                                true
                            }
                            R.id.edit -> {
                                moreVert.isChecked = false
                                onInteractionListener.onEdit(event)
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