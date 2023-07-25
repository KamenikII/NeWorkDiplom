package ru.netology.neworkedin.adapters

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ru.netology.neworkedin.databinding.FragmentCardJobBinding
import ru.netology.neworkedin.dataclass.Job

class JobViewHolder(
    private val binding: FragmentCardJobBinding,
    private val onInteractionListener: OnInteractionListenerJob,
) : RecyclerView.ViewHolder(binding.root) {


    fun renderingPostStructure(job: Job) {
        with(binding) {
            jobOrganization.text = job.name
            jobPosition.text = job.position
            val workingPeriod = "${job.start.take(10)}\n${job.finish?.take(10) ?: "..."}"
            jobWorking.text = workingPeriod
            jobLink.isVisible = (job.link != null)

            if (job.link != null) {
                jobLink.text = job.link
            }

            jobEdit.isVisible = onInteractionListener.myOrNo(job)
            jobRemove.isVisible = onInteractionListener.myOrNo(job)
            postListeners(job)
        }
    }

    private fun postListeners(job: Job) {
        with(binding) {

            jobLink.setOnClickListener {
                onInteractionListener.onLink(job)
            }

            jobEdit.setOnClickListener{
                onInteractionListener.onEdit(job)
            }

            jobRemove.setOnClickListener{
                onInteractionListener.onRemove(job)
            }
        }
    }
}