package ru.netology.neworkedin.adapters

import ru.netology.neworkedin.dataclass.User

/** ИНТЕРФЕЙС ОТВЕЧАЮЩИЙ ЗА МЕТОДЫ, КОТОРЫЕ "СЛУШАЮТ" НАЖАТИЯ ПОЛЬЗОВАТЕЛЯ*/

interface OnInteractionListenerUsers {
    fun onTap(user: User) {}
}