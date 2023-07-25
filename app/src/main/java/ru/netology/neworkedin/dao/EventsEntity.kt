package ru.netology.neworkedin.dao

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.neworkedin.utils.Converts
import ru.netology.neworkedin.dataclass.*

@Entity
class EventsEntity(
    @PrimaryKey
    val id:Long,
    val authorId:Long,
    val author:String,
    val authorAvatar:String? = null,
    val authorJob:String? = null,
    val content:String,
    val datetime:String,
    val published:String,
    val typeEvent: EventType,
    val likeOwnerIds:  String?,
    val likedByMe:Boolean = false,
    val speakerIds:  String?,
    val participantsIds:  String?,
    val participatedByMe:Boolean = false,

    @Embedded
    val attachment: AttachmentEmbeddable? = null,
    val link:String? = null,
    val ownedByMe:Boolean = false,

)  {

    fun toDto() = Event(
        id, authorId, author, authorAvatar, authorJob, content, datetime, published, null, typeEvent, Converts.toListDto(likeOwnerIds),
        likedByMe, Converts.toListDto(speakerIds), Converts.toListDto(participantsIds), participatedByMe, attachment?.toDto(), link, ownedByMe, null
    )

    companion object {
        fun fromDto(dto: Event) =
            EventsEntity(
                dto.id, dto.authorId, dto.author, dto.authorAvatar, dto.authorJob, dto.content, dto.datetime, dto.published, dto.type, Converts.fromListDto(dto.likeOwnerIds),
                dto.likedByMe, Converts.fromListDto(dto.speakerIds), Converts.fromListDto(dto.participantsIds), dto.participatedByMe, AttachmentEmbeddable.fromDto(dto.attachment), dto.link, dto.ownedByMe
            )
    }
}

fun List<EventsEntity>.toDto(): List<Event> = map(EventsEntity::toDto)
fun List<Event>.toEntity(): List<EventsEntity> = map(EventsEntity::fromDto)