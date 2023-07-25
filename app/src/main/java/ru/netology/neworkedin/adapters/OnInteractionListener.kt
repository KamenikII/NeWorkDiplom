package ru.netology.neworkedin.adapters

import android.widget.VideoView
import ru.netology.neworkedin.dataclass.Post

/** ИНТЕРФЕЙС ОТВЕЧАЮЩИЙ ЗА МЕТОДЫ, КОТОРЫЕ "СЛУШАЮТ" НАЖАТИЯ ПОЛЬЗОВАТЕЛЯ*/

interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onShare(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onPlayPost(post: Post, videoView: VideoView? = null) {}
    fun onLink(post: Post) {}
    fun onPreviewAttachment(post: Post) {}
    fun onTapAvatar(post: Post) {}
}