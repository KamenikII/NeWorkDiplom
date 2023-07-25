package ru.netology.neworkedin.utils

/** КЛАСС ОТВЕЧАЮЩИЙ ЗА ПРИЛОЖЕНИЯ К ПОСТУ */

object Converts {

    fun fromListDto(list: List<Long>?): String {
        if (list == null) return ""
        return list.toString()
    }

    fun toListDto(data: String?): List<Long> {
        return if (data == "[]") emptyList()

        else {
            val substr = data?.substring(1, data.length - 1)
            substr?.split(", ")?.map { it.toLong() } ?: emptyList()
        }
    }

}