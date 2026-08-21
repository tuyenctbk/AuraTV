package com.example.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Environment
import android.os.StatFs
import com.example.data.db.AppDao
import com.example.data.db.AppMetadataEntity
import com.example.data.db.SettingsDao
import com.example.data.db.UserSettingsEntity
import com.example.data.model.AppItem
import com.example.data.model.AppType
import com.example.data.model.StorageInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

import kotlinx.coroutines.flow.map

class LauncherRepository(
    private val context: Context,
    private val appDao: AppDao,
    private val settingsDao: SettingsDao
) {
    val settingsFlow: Flow<UserSettingsEntity?> = settingsDao.getSettingsFlow()

    val appsFlow: Flow<List<AppItem>> = appDao.getAllMetadata().map { metadataList ->
        scanInstalledApps(metadataList.associateBy { it.packageName })
    }

    suspend fun getSettings(): UserSettingsEntity {
        return withContext(Dispatchers.IO) {
            settingsDao.getSettings() ?: UserSettingsEntity().also {
                settingsDao.insertOrUpdate(it)
            }
        }
    }

    suspend fun updateSettings(settings: UserSettingsEntity) {
        withContext(Dispatchers.IO) {
            settingsDao.insertOrUpdate(settings)
        }
    }

    suspend fun setAppHidden(packageName: String, hidden: Boolean) {
        withContext(Dispatchers.IO) {
            val existing = appDao.getMetadata(packageName)
            if (existing != null) {
                appDao.setHidden(packageName, hidden)
            } else {
                appDao.insertOrUpdate(
                    AppMetadataEntity(packageName = packageName, isHidden = hidden)
                )
            }
        }
    }

    suspend fun setAppCustomBanner(packageName: String, bannerPath: String?) {
        withContext(Dispatchers.IO) {
            val existing = appDao.getMetadata(packageName)
            if (existing != null) {
                appDao.setCustomBanner(packageName, bannerPath)
            } else {
                appDao.insertOrUpdate(
                    AppMetadataEntity(packageName = packageName, customBannerPath = bannerPath)
                )
            }
        }
    }

    suspend fun setAppCategory(packageName: String, category: String?) {
        withContext(Dispatchers.IO) {
            val existing = appDao.getMetadata(packageName)
            if (existing != null) {
                appDao.setCategory(packageName, category)
            } else {
                appDao.insertOrUpdate(
                    AppMetadataEntity(packageName = packageName, category = category)
                )
            }
        }
    }

    suspend fun setFastLaunchKey(packageName: String, key: Int?) {
        withContext(Dispatchers.IO) {
            if (key != null) {
                appDao.clearFastLaunchKey(key)
            }
            val existing = appDao.getMetadata(packageName)
            if (existing != null) {
                appDao.setFastLaunchKey(packageName, key)
            } else {
                appDao.insertOrUpdate(
                    AppMetadataEntity(packageName = packageName, fastLaunchKey = key)
                )
            }
        }
    }

    suspend fun clearAllMetadata() {
        withContext(Dispatchers.IO) {
            appDao.clearAll()
        }
    }

    fun getStorageInfo(): StorageInfo {
        return try {
            val path = Environment.getDataDirectory()
            val stat = StatFs(path.path)
            val blockSize = stat.blockSizeLong
            val totalBlocks = stat.blockCountLong
            val availableBlocks = stat.availableBlocksLong

            val totalBytes = totalBlocks * blockSize
            val freeBytes = availableBlocks * blockSize
            val usedBytes = totalBytes - freeBytes
            val percentage = if (totalBytes > 0) (usedBytes.toFloat() / totalBytes.toFloat()) * 100f else 0f

            StorageInfo(totalBytes, freeBytes, usedBytes, percentage)
        } catch (e: Exception) {
            StorageInfo(0, 0, 0, 0f)
        }
    }

    private fun scanInstalledApps(metadataMap: Map<String, AppMetadataEntity>): List<AppItem> {
        val pm = context.packageManager

        // Query Leanback TV Launcher intents
        val leanbackIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)
        }
        val leanbackActivities = pm.queryIntentActivities(leanbackIntent, 0)
            .associateBy { it.activityInfo.packageName }

        // Query standard Launcher intents
        val launcherIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val launcherActivities = pm.queryIntentActivities(launcherIntent, 0)

        val ownPackageName = context.packageName

        val autoFolderBannersAura = File(Environment.getExternalStorageDirectory(), "AuraTV/Banners")
        val autoFolderBannersLegacy = File(Environment.getExternalStorageDirectory(), "GhostLauncher/Banners")

        val items = mutableListOf<AppItem>()
        val processedPackages = mutableSetOf<String>()

        // Combine activities
        val allActivities = (leanbackActivities.values + launcherActivities).distinctBy { it.activityInfo.packageName }

        for (resolveInfo in allActivities) {
            val pkgName = resolveInfo.activityInfo.packageName
            if (pkgName == ownPackageName) continue // Don't show launcher inside launcher
            if (processedPackages.contains(pkgName)) continue
            processedPackages.add(pkgName)

            val isLeanback = leanbackActivities.containsKey(pkgName)
            val isSystem = (resolveInfo.activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

            val appType = when {
                isLeanback -> AppType.LEANBACK
                isSystem -> AppType.SYSTEM
                else -> AppType.SIDELOADED
            }

            val meta = metadataMap[pkgName]

            val label = meta?.customTitle ?: resolveInfo.loadLabel(pm).toString()
            val iconDrawable: Drawable? = resolveInfo.loadIcon(pm)

            var bannerDrawable: Drawable? = null
            try {
                if (isLeanback) {
                    bannerDrawable = resolveInfo.activityInfo.loadBanner(pm)
                        ?: resolveInfo.activityInfo.applicationInfo.loadBanner(pm)
                }
            } catch (e: Exception) {
                // Ignore missing banner
            }

            // Check auto folder banner: /AuraTV/Banners/pkgName.(png|jpg|jpeg|webp) or legacy /GhostLauncher/Banners/
            var customBannerPath = meta?.customBannerPath
            if (customBannerPath.isNullOrEmpty()) {
                val bannerFolders = listOf(autoFolderBannersAura, autoFolderBannersLegacy)
                val extensions = listOf("png", "jpg", "jpeg", "webp")
                for (folder in bannerFolders) {
                    if (folder.exists()) {
                        val matchedFile = extensions.map { File(folder, "$pkgName.$it") }.firstOrNull { it.exists() }
                        if (matchedFile != null) {
                            customBannerPath = matchedFile.absolutePath
                            break
                        }
                    }
                }
            }

            val defaultCategory = when (appType) {
                AppType.LEANBACK -> "TV Apps"
                AppType.SIDELOADED -> "Sideloaded"
                AppType.SYSTEM -> "System"
            }

            val category = meta?.category ?: defaultCategory
            val isHidden = meta?.isHidden ?: false
            val fastLaunchKey = meta?.fastLaunchKey

            var versionName = ""
            try {
                val pInfo = pm.getPackageInfo(pkgName, 0)
                versionName = pInfo.versionName ?: ""
            } catch (e: Exception) {
                // ignore
            }

            items.add(
                AppItem(
                    packageName = pkgName,
                    activityName = resolveInfo.activityInfo.name,
                    label = label,
                    iconDrawable = iconDrawable,
                    bannerDrawable = bannerDrawable,
                    customBannerPath = customBannerPath,
                    appType = appType,
                    category = category,
                    isHidden = isHidden,
                    fastLaunchKey = fastLaunchKey,
                    isSystem = isSystem,
                    versionName = versionName
                )
            )
        }

        return items.sortedWith(
            compareBy<AppItem> { if (it.category == "Favorites") 0 else 1 }
                .thenBy { it.label.lowercase() }
        )
    }

    suspend fun exportBackupJson(metadataList: List<AppMetadataEntity>, settings: UserSettingsEntity): String {
        return withContext(Dispatchers.IO) {
            val list = if (metadataList.isEmpty()) appDao.getAllMetadataList() else metadataList
            val root = JSONObject()
            val settingsObj = JSONObject().apply {
                put("columnsCount", settings.columnsCount)
                put("showLabels", settings.showLabels)
                put("cardRatio", settings.cardRatio)
                put("wallpaperUri", settings.wallpaperUri ?: "")
                put("wallpaperDimLevel", settings.wallpaperDimLevel)
                put("reduceMotion", settings.reduceMotion)
                put("showStorageWidget", settings.showStorageWidget)
                put("showClockWidget", settings.showClockWidget)
                put("categoriesOrder", settings.categoriesOrder)
            }
            root.put("settings", settingsObj)

            val metaArray = JSONArray()
            for (m in list) {
                val item = JSONObject().apply {
                    put("packageName", m.packageName)
                    put("customBannerPath", m.customBannerPath ?: "")
                    put("customTitle", m.customTitle ?: "")
                    put("category", m.category ?: "")
                    put("isHidden", m.isHidden)
                    put("fastLaunchKey", m.fastLaunchKey ?: -1)
                }
                metaArray.put(item)
            }
            root.put("apps", metaArray)
            root.toString(2)
        }
    }

    suspend fun importBackupJson(jsonString: String) {
        importConfigJson(jsonString)
    }

    suspend fun exportConfigJson(metadataList: List<AppMetadataEntity>, settings: UserSettingsEntity): String {
        return exportBackupJson(metadataList, settings)
    }

    suspend fun importConfigJson(jsonString: String) {
        withContext(Dispatchers.IO) {
            val root = JSONObject(jsonString)
            if (root.has("settings")) {
                val s = root.getJSONObject("settings")
                val current = getSettings()
                val updated = current.copy(
                    columnsCount = s.optInt("columnsCount", current.columnsCount),
                    showLabels = s.optBoolean("showLabels", current.showLabels),
                    cardRatio = s.optString("cardRatio", current.cardRatio),
                    wallpaperUri = s.optString("wallpaperUri", current.wallpaperUri),
                    wallpaperDimLevel = s.optDouble("wallpaperDimLevel", current.wallpaperDimLevel.toDouble()).toFloat(),
                    reduceMotion = s.optBoolean("reduceMotion", current.reduceMotion),
                    showStorageWidget = s.optBoolean("showStorageWidget", current.showStorageWidget),
                    showClockWidget = s.optBoolean("showClockWidget", current.showClockWidget),
                    categoriesOrder = s.optString("categoriesOrder", current.categoriesOrder)
                )
                settingsDao.insertOrUpdate(updated)
            }

            if (root.has("apps")) {
                val arr = root.getJSONArray("apps")
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    val pkg = obj.optString("packageName")
                    if (pkg.isNotEmpty()) {
                        val banner = obj.optString("customBannerPath").takeIf { it.isNotEmpty() }
                        val title = obj.optString("customTitle").takeIf { it.isNotEmpty() }
                        val cat = obj.optString("category").takeIf { it.isNotEmpty() }
                        val isHidden = obj.optBoolean("isHidden", false)
                        val key = obj.optInt("fastLaunchKey", -1).takeIf { it >= 0 }

                        appDao.insertOrUpdate(
                            AppMetadataEntity(
                                packageName = pkg,
                                customBannerPath = banner,
                                customTitle = title,
                                category = cat,
                                isHidden = isHidden,
                                fastLaunchKey = key
                            )
                        )
                    }
                }
            }
        }
    }
}
