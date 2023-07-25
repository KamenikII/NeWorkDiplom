package ru.netology.neworkedin.auth

data class AuthState(val id: Long = 0, val token: String? = null, val name: String? = null)