package ru.netology.neworkedin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import ru.netology.neworkedin.databinding.FragmentCardEventBinding
import ru.netology.neworkedin.dataclass.*

/** Event... ОТВЕЧАЕТ ЗА РЕНДЕРИНГ ИВЕНТОВ, ИХ ВНЕШНИЙ ВИД, "СЛУШАЕТ" НАЖАТИЯ */

class EventAdapter(
    private val onInteractionListener: OnInteractionListenerEvent
) : ListAdapter<Event,EventViewHolder>(EventDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding =
            FragmentCardEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val post = getItem(position)
        holder.renderingPostStructure(post)
    }

    override fun onViewRecycled(holder: EventViewHolder) {
        super.onViewRecycled(holder)
        holder.binding.videoAttachment.stopPlayback()
        holder.binding.videoAttachment.setVideoURI(null)
    }
}