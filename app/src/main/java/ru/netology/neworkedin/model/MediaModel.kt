package ru.netology.neworkedin.model

import android.net.Uri
import ru.netology.neworkedin.dataclass.*
import java.io.File

/**МОДЕЛЬ ДЛЯ МЕДИА*/

data class MediaModel(val uri: Uri? = null, val file: File? = null, val attachmentType: AttachmentType? = null)