package ru.netology.neworkedin.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.neworkedin.dataclass.*

/** ИНТЕРФЕЙС, В КОТОРОМ ОПИСАНЫ ВСЕ МЕТОДЫ РАБОТЫ С ПОСТАМИ, РАБОТОЙ И ТД */

interface Repository {

    val data: Flow<List<Post>>
    val dataUsers: Flow<List<User>>
    val dataEvents: Flow<List<Event>>
    val dataJobs: Flow<List<Job>>

    fun getNewerCount(id: Long): Flow<Int>
    suspend fun showNewPosts()
    suspend fun edit(post: Post)
    suspend fun getAllAsync()
    suspend fun removeByIdAsync(id: Long)
    suspend fun saveAsync(post: Post)
    suspend fun saveWithAttachment(post: Post, upload: MediaUpload, attachmentType: AttachmentType)
    suspend fun likeByIdAsync(post: Post)
    suspend fun upload(upload: MediaUpload): MediaResponse
    suspend fun getUsers()
    suspend fun getUserBuId(id: Long)
    suspend fun getAllEvents()
    suspend fun saveEvents(event: Event)
    suspend fun saveEventsWithAttachment(event: Event, upload: MediaUpload, attachmentType: AttachmentType)
    suspend fun removeEventsById(id: Long)
    suspend fun likeByIdEvents(event: Event)
    suspend fun joinByIdEvents(event: Event)
    suspend fun getJobs(id: Long)
    suspend fun getMyJobs(id: Long)
    suspend fun saveJob(job: Job)
    suspend fun removeJobById(id: Long)
}