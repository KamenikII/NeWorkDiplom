package ru.netology.neworkedin.dataclass

data class PostCreateRequest(
    val id: Long,
    val content: String,
    val coords: Coordinates? = null,
    val link: String?,
    val attachment: Attachment? = null,
    val mentionIds: List<Long>? = null
)