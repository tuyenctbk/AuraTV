package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_entity")
data class AppEntity(
    @PrimaryKey val packageName: String,
    val customBannerPath: String? = null,
    val customTitle: String? = null,
    val category: String? = null,
    val isHidden: Boolean = false,
    val fastLaunchKey: Int? = null,
    val displayOrder: Int = 0,
    val launchCount: Int = 0,
    val lastLaunchedTime: Long = 0L,
    val installedTime: Long = 0L
)

typealias AppMetadataEntity = AppEntity

