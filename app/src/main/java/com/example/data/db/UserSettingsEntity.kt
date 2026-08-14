package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val columnsCount: Int = 5,
    val showLabels: Boolean = true,
    val cardRatio: String = "16:9", // "16:9" or "1:1"
    val wallpaperUri: String? = "preset_dark_cyber",
    val wallpaperDimLevel: Float = 0.5f,
    val reduceMotion: Boolean = false,
    val showStorageWidget: Boolean = true,
    val showClockWidget: Boolean = true,
    val hasAcceptedTransparency: Boolean = false,
    val vaultSequence: String = "UP,UP,DOWN,LEFT",
    val autoHideSideloaded: Boolean = false,
    val categoriesOrder: String = "Favorites,TV Apps,Sideloaded,System",
    val accentColorHex: String = "#D0BCFF",
    val usePaletteExtraction: Boolean = true,
    val showCategoryHeaders: Boolean = true,
    val screensaverTimeoutMinutes: Int = 5,
    val isOnboardingCompleted: Boolean = false,
    val appLaunchCount: Int = 0,
    val hasDismissedSharePrompt: Boolean = false,
    val hasDismissedRatePrompt: Boolean = false
)
