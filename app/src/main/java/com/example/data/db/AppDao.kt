package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Query("SELECT * FROM app_entity")
    fun getAllMetadata(): Flow<List<AppEntity>>

    @Query("SELECT * FROM app_entity")
    suspend fun getAllMetadataList(): List<AppEntity>

    @Query("SELECT * FROM app_entity WHERE packageName = :packageName")
    suspend fun getMetadata(packageName: String): AppEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(metadata: AppEntity)

    @Query("UPDATE app_entity SET isHidden = :hidden WHERE packageName = :packageName")
    suspend fun setHidden(packageName: String, hidden: Boolean)

    @Query("UPDATE app_entity SET customBannerPath = :bannerPath WHERE packageName = :packageName")
    suspend fun setCustomBanner(packageName: String, bannerPath: String?)

    @Query("UPDATE app_entity SET category = :category WHERE packageName = :packageName")
    suspend fun setCategory(packageName: String, category: String?)

    @Query("UPDATE app_entity SET fastLaunchKey = :key WHERE packageName = :packageName")
    suspend fun setFastLaunchKey(packageName: String, key: Int?)

    @Query("UPDATE app_entity SET fastLaunchKey = NULL WHERE fastLaunchKey = :key")
    suspend fun clearFastLaunchKey(key: Int)

    @Query("DELETE FROM app_entity WHERE packageName = :packageName")
    suspend fun delete(packageName: String)

    @Query("DELETE FROM app_entity")
    suspend fun clearAll()
}
