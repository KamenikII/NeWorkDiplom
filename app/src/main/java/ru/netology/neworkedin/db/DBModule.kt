package ru.netology.neworkedin.db

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.netology.neworkedin.dao.PostDaoRoom
import ru.netology.neworkedin.dao.RemoteKeyDao
import javax.inject.Singleton

/**класс, предоставляющий зависимости с БД*/

@InstallIn(SingletonComponent::class)
@Module
class DBModule {

    @Singleton
    @Provides
    fun provideDb(
        @ApplicationContext
        context: Context
    ): AppDBRoom = Room.databaseBuilder(context, AppDBRoom::class.java, "app.db")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun providePostDao(
        appDb: AppDBRoom
    ):PostDaoRoom = appDb.postDaoRoom()

    @Provides
    fun providePostRemoteKeyDao(
        appDb: AppDBRoom
    ): RemoteKeyDao = appDb.remoteKeyDao()
}