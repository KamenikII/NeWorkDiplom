package ru.netology.neworkedin.dao

import androidx.room.*

@Entity
data class PostKeyEntity (
    @PrimaryKey
    val type: KeyType,
    val idPost: Long,
) {
    enum class KeyType {
        AFTER,
        BEFORE,
    }
}