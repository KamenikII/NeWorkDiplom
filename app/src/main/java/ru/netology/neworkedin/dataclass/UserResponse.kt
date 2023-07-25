package ru.netology.neworkedin.dataclass

data class UserResponse(
    val id: Long,
    val login: String,
    val name: String,
    val avatar: String? = null
)