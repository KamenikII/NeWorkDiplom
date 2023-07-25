package ru.netology.neworkedin.dataclass

data class User(
    val id: Long,
    val login: String,
    val name: String,
    val avatar: String? = null,
)