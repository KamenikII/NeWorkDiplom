package ru.netology.neworkedin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import ru.netology.neworkedin.databinding.FragmentCardJobBinding
import ru.netology.neworkedin.dataclass.Job

/** Job... ОТВЕЧАЕТ ЗА РЕНДЕРИНГ РАБОТЫ, ИХ ВНЕШНИЙ ВИД, "СЛУШАЕТ" НАЖАТИЯ */

class JobAdapter(
    private val onInteractionListener: OnInteractionListenerJob
) : ListAdapter<Job,JobViewHolder>(JobDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding =
            FragmentCardJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JobViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = getItem(position)
        holder.renderingPostStructure(job)
    }
}