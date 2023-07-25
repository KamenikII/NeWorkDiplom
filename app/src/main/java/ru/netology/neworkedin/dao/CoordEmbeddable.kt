package ru.netology.neworkedin.dao

import ru.netology.neworkedin.dataclass.*

data class CoordEmbeddable(
    val latitude : String?,
    val longitude : String?,
) {
    fun toDto() =
        Coordinates(latitude, longitude)

    companion object {
        fun fromDto(dto: Coordinates?) = dto?.let {
            CoordEmbeddable(it.lat, it.long)
        }
    }
}