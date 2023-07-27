package ru.netology.neworkedin.dao

import androidx.room.*

@Dao
interface RemoteKeyDao {

    @Query("SELECT max(`idPost`) FROM PostKeyEntity")
    suspend fun max(): Long?

    @Query("SELECT min(`idPost`) FROM PostKeyEntity")
    suspend fun min(): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(postRemoteKeyEntity: PostKeyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(postRemoteKeyEntity: List<PostKeyEntity>)

    @Query("DELETE FROM PostKeyEntity")
    suspend fun clear()
}