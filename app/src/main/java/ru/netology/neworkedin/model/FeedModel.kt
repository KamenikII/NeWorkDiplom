package ru.netology.neworkedin.model

import ru.netology.neworkedin.dataclass.*

/** МОДЕЛЬ ПОСТОВ, СПИСОК, ОШИБКИ И ТП. не dragger hilt*/

data class FeedModel(
    val posts: List<Post> = emptyList(),
    val empty: Boolean = false,
)