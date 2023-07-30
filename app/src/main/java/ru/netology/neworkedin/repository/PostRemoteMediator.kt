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
                    keyDao.max()?.let {
                        id -> apiService.getAfter(id, state.config.pageSize)
                    } ?: apiService.getLatest(state.config.initialLoadSize)
                }

                LoadType.APPEND -> {
                    val id = keyDao.min() ?: return MediatorResult.Success(
                        endOfPaginationReached = false)
                    apiService.getBefore(id, state.config.pageSize)
                }

                LoadType.PREPEND -> {
                    val id = keyDao.max() ?: return MediatorResult.Success(
                        endOfPaginationReached = false)
                    apiService.getAfter(id, state.config.pageSize)
                    return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                }
            }

            if (!result.isSuccessful) {
                throw HttpException(result)
            }

            val data = result.body().orEmpty()

            postDao.insert(
                data.map {
                    PostEntity.fromDto(it)
                }
            )

            appDB.withTransaction {
                when (loadType) {
                    LoadType.REFRESH -> {
                        if (keyDao.max() == null ) {
                            postDao.clear()
                            keyDao.insert(
                                listOf(
                                    PostKeyEntity(
                                        type = PostKeyEntity.KeyType.AFTER,
                                        idPost = data.first().id
                                    ),
                                    PostKeyEntity(
                                        type = PostKeyEntity.KeyType.BEFORE,
                                        idPost = data.last().id
                                    ),
                                )
                            )
                        } else {
                            keyDao.insert(
                                PostKeyEntity(
                                    type = PostKeyEntity.KeyType.AFTER,
                                    idPost = data.first().id,
                                )
                            )
                        }
                    }

                    LoadType.PREPEND -> {
                        keyDao.insert(
                            PostKeyEntity(
                                type = PostKeyEntity.KeyType.AFTER,
                                idPost = data.first().id
                            ),
                        )
                    }

                    LoadType.APPEND -> {
                        keyDao.insert(
                            PostKeyEntity(
                                type = PostKeyEntity.KeyType.BEFORE,
                                idPost = data.last().id
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