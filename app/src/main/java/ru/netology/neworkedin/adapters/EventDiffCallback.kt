package ru.netology.neworkedin.adapters

import androidx.recyclerview.widget.DiffUtil
import ru.netology.neworkedin.dataclass.Event

class EventDiffCallback: DiffUtil.ItemCallback<Event>() {
    override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
        return oldItem == newItem
    }
}