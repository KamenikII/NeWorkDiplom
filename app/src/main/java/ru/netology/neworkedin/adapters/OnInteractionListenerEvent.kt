package ru.netology.neworkedin.adapters

import android.widget.VideoView
import ru.netology.neworkedin.dataclass.Event

/** ИНТЕРФЕЙС ОТВЕЧАЮЩИЙ ЗА МЕТОДЫ, КОТОРЫЕ "СЛУШАЮТ" НАЖАТИЯ ПОЛЬЗОВАТЕЛЯ*/

interface OnInteractionListenerEvent {
    fun onLike(event: Event) {}
    fun onShare(event: Event) {}
    fun onEdit(event: Event) {}
    fun onRemove(event: Event) {}
    fun onPlayPost(event: Event, videoView: VideoView? = null) {}
    fun onLink(event: Event) {}
    fun onPreviewAttachment(event: Event) {}
    fun onSpeakersAction(event: Event) {}
    fun onPartyAction(event: Event) {}
    fun onJoinAction(event: Event) {}
    fun onTapAvatar(event: Event) {}
}