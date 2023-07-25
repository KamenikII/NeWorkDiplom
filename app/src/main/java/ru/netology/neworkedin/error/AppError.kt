package ru.netology.neworkedin.error

/** КАСТОМНЫЕ ОШИБКИ */

sealed class AppError(var code: String): RuntimeException()

class ApiError(code: String): AppError(code)

object NetworkError : AppError("error_network")

object UnknownError: AppError("error_unknown")
