package ru.netology.neworkedin.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.netology.neworkedin.dao.*

/** КЛАСС, ОТВЕЧАЮЩИЙ ЗА РАБОТУ БД */

@Database(entities = [PostEntity::class, UserEntity::class, EventsEntity::class, JobEntity::class, PostKeyEntity::class], version = 1)
abstract class AppDBRoom : RoomDatabase() {
    abstract fun postDaoRoom(): PostDaoRoom
    abstract fun remoteKeyDao(): RemoteKeyDao
}