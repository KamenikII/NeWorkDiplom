package ru.netology.neworkedin.model

/** Состояния поста / запросов */

sealed interface FeedModelState {
    object Loading : FeedModelState
    object Error : FeedModelState
    object Refresh : FeedModelState
    object Idle : FeedModelState
    object ShadowIdle : FeedModelState
}