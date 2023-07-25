package ru.netology.neworkedin.adapters

import ru.netology.neworkedin.dataclass.Job

/** ИНТЕРФЕЙС ОТВЕЧАЮЩИЙ ЗА МЕТОДЫ, КОТОРЫЕ "СЛУШАЮТ" НАЖАТИЯ ПОЛЬЗОВАТЕЛЯ*/

interface OnInteractionListenerJob {
    fun onEdit(job: Job) {}
    fun onRemove(job: Job) {}
    fun onLink(job: Job) {}
    fun myOrNo(job: Job):Boolean {return true}
}