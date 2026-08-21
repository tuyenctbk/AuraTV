package com.example.data.model

import android.graphics.drawable.Drawable

data class AppItem(
    val packageName: String,
    val activityName: String,
    val label: String,
    val iconDrawable: Drawable?,
    val bannerDrawable: Drawable?,
    val customBannerPath: String? = null,
    val appType: AppType,
    val category: String,
    val isHidden: Boolean = false,
    val fastLaunchKey: Int? = null,
    val isSystem: Boolean = false,
    val versionName: String = "",
    val versionCode: Long = 0L,
    val firstInstallTime: Long = 0L,
    val lastUpdateTime: Long = 0L,
    val launchCount: Int = 0,
    val lastLaunchedTime: Long = 0L
)

