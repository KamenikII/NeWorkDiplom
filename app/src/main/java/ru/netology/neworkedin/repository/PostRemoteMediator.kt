package ru.netology.neworkedin.repository

import androidx.paging.*
import androidx.room.withTransaction
import retrofit2.HttpException
import ru.netology.neworkedin.api.ApiService
import ru.netology.neworkedin.dao.PostDaoRoom
import ru.netology.neworkedin.dao.PostEntity
import ru.netology.neworkedin.dao.PostKeyEntity
import ru.netology.neworkedin.dao.RemoteKeyDao
import ru.netology.neworkedin.db.AppDBRoom
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator(
    private val apiService: ApiService,
    private val postDao: PostDaoRoom,
    private val keyDao: RemoteKeyDao,
    private val appDB: AppDBRoom,

    ) : RemoteMediator<Int, PostEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
    ): MediatorResult {

        try {
            val result = when (loadType) {
                LoadType.REFRESH -> {
                    apiService.getLatest(state.config.pageSize)
                }

                LoadType.APPEND -> {
                    val id = keyDao.min() ?: return MediatorResult.Success(false)
                    apiService.getBefore(id, state.config.pageSize)
                }

                LoadType.PREPEND -> {
                    val id = keyDao.max() ?: return MediatorResult.Success(false)
                    apiService.getAfter(id, state.config.pageSize)
                }
            }

            if (!result.isSuccessful) {
                throw HttpException(result)
            }

            val data = result.body().orEmpty()

            appDB.withTransaction {
                when (loadType) {
                    LoadType.REFRESH -> {
                        postDao.clear()
                        keyDao.insert(
                            listOf(
                                PostKeyEntity(
                                    PostKeyEntity.KeyType.AFTER,
                                    data.first().id
                                ),
                                PostKeyEntity(
                                    PostKeyEntity.KeyType.BEFORE,
                                    data.last().id
                                ),
                            )
                        )
                    }

                    LoadType.PREPEND -> {
                        keyDao.insert(
                            PostKeyEntity(
                                PostKeyEntity.KeyType.AFTER,
                                data.first().id
                            ),
                        )
                    }

                    LoadType.APPEND -> {
                        keyDao.insert(
                            PostKeyEntity(
                                PostKeyEntity.KeyType.BEFORE,
                                data.last().id
                            ),
                        )
                    }
                }

                postDao.insert(data.map { PostEntity.fromDto(it) })
            }

            return MediatorResult.Success(data.isEmpty())
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        }
    }
}