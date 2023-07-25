package ru.netology.neworkedin.adapters

import androidx.recyclerview.widget.DiffUtil
import ru.netology.neworkedin.dataclass.Job

class JobDiffCallback : DiffUtil.ItemCallback<Job>() {
    override fun areItemsTheSame(oldItem: Job, newItem: Job): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Job, newItem: Job): Boolean {
        return oldItem == newItem
    }
}