package com.example.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.example.data.db.AppDao
import com.example.data.db.AppEntity
import com.example.data.model.AppItem
import com.example.data.model.AppType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AppRepository(
    private val context: Context,
    private val appDao: AppDao
) {
    private val pm: PackageManager = context.packageManager

    val allAppsFlow: Flow<List<AppItem>> = appDao.getAllMetadata().map { customizations ->
        getApplications(customizations.associateBy { it.packageName })
    }

    val leanbackAppsFlow: Flow<List<AppItem>> = allAppsFlow.map { list ->
        list.filter { it.appType == AppType.LEANBACK }
    }

    val sideloadedAppsFlow: Flow<List<AppItem>> = allAppsFlow.map { list ->
        list.filter { it.appType == AppType.SIDELOADED }
    }

    suspend fun getApplications(
        customMap: Map<String, AppEntity> = emptyMap()
    ): List<AppItem> = withContext(Dispatchers.IO) {
        val appList = mutableListOf<AppItem>()
        val processedPackages = mutableSetOf<String>()

        // 1. Query Leanback TV Apps using CATEGORY_LEANBACK_LAUNCHER
        val leanbackIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)
        }
        val leanbackActivities = try {
            pm.queryIntentActivities(leanbackIntent, PackageManager.MATCH_ALL)
        } catch (e: Exception) {
            emptyList()
        }

        for (resolveInfo in leanbackActivities) {
            val activityInfo = resolveInfo.activityInfo ?: continue
            val pkgName = activityInfo.packageName
            if (pkgName == context.packageName) continue

            processedPackages.add(pkgName)

            val custom = customMap[pkgName]
            val appInfo = activityInfo.applicationInfo

            val icon = try { resolveInfo.loadIcon(pm) } catch (e: Exception) { null }
            val banner = try { activityInfo.loadBanner(pm) ?: activityInfo.applicationInfo.loadBanner(pm) } catch (e: Exception) { null }
            val label = custom?.customTitle ?: try {
                resolveInfo.loadLabel(pm).toString()
            } catch (e: Exception) { pkgName }

            val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

            appList.add(
                AppItem(
                    packageName = pkgName,
                    activityName = activityInfo.name,
                    label = label,
                    iconDrawable = icon,
                    bannerDrawable = banner,
                    customBannerPath = custom?.customBannerPath,
                    appType = AppType.LEANBACK,
                    category = custom?.category ?: "TV Apps",
                    isHidden = custom?.isHidden ?: false,
                    fastLaunchKey = custom?.fastLaunchKey,
                    isSystem = isSystem
                )
            )
        }

        // 2. Query Sideloaded Mobile Apps using CATEGORY_LAUNCHER
        val launcherIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val launcherActivities = try {
            pm.queryIntentActivities(launcherIntent, PackageManager.MATCH_ALL)
        } catch (e: Exception) {
            emptyList()
        }

        for (resolveInfo in launcherActivities) {
            val activityInfo = resolveInfo.activityInfo ?: continue
            val pkgName = activityInfo.packageName
            if (pkgName == context.packageName || processedPackages.contains(pkgName)) continue

            processedPackages.add(pkgName)

            val custom = customMap[pkgName]
            val appInfo = activityInfo.applicationInfo

            val icon = try { resolveInfo.loadIcon(pm) } catch (e: Exception) { null }
            val banner = try { activityInfo.loadBanner(pm) ?: appInfo.loadBanner(pm) } catch (e: Exception) { null }
            val label = custom?.customTitle ?: try {
                resolveInfo.loadLabel(pm).toString()
            } catch (e: Exception) { pkgName }

            val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

            appList.add(
                AppItem(
                    packageName = pkgName,
                    activityName = activityInfo.name,
                    label = label,
                    iconDrawable = icon,
                    bannerDrawable = banner,
                    customBannerPath = custom?.customBannerPath,
                    appType = AppType.SIDELOADED,
                    category = custom?.category ?: "Sideloaded",
                    isHidden = custom?.isHidden ?: false,
                    fastLaunchKey = custom?.fastLaunchKey,
                    isSystem = isSystem
                )
            )
        }

        appList.sortedBy { it.label.lowercase() }
    }
}
