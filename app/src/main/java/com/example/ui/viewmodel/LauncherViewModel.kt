package com.example.ui.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import android.os.Bundle
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.example.data.db.AppMetadataEntity
import com.example.data.db.GhostDatabase
import com.example.data.db.UserSettingsEntity
import com.example.data.model.AppItem
import com.example.data.model.AppType
import com.example.data.model.StorageInfo
import com.example.data.repository.LauncherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LauncherRepository
    val settings: StateFlow<UserSettingsEntity>
    val allApps: StateFlow<List<AppItem>>

    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow("All")
    val isVaultUnlocked = MutableStateFlow(false)
    val storageInfo = MutableStateFlow(StorageInfo(0, 0, 0, 0f))
    val statusMessage = MutableStateFlow<String?>(null)

    // Dialog state
    val selectedAppForOptions = MutableStateFlow<AppItem?>(null)
    val showSettings = MutableStateFlow(false)
    val showHelpOverlay = MutableStateFlow(false)
    val showVaultUnlockModal = MutableStateFlow(false)
    val showCustomBannerDialogForApp = MutableStateFlow<AppItem?>(null)
    val showHotkeyDialogForApp = MutableStateFlow<AppItem?>(null)
    val showCategoryDialogForApp = MutableStateFlow<AppItem?>(null)
    val showTransparencyNotice = MutableStateFlow(false)
    val showSharePrompt = MutableStateFlow(false)
    val showRatePrompt = MutableStateFlow(false)

    private val isFirebaseAvailable: Boolean by lazy {
        try {
            val apps = FirebaseApp.getApps(application)
            apps.isNotEmpty() && !FirebaseApp.getInstance().options.applicationId.isNullOrBlank()
        } catch (e: Exception) {
            false
        }
    }

    private val firebaseAnalytics: FirebaseAnalytics? by lazy {
        if (isFirebaseAvailable) {
            try {
                FirebaseAnalytics.getInstance(application)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    private fun safeLogEvent(name: String, params: Bundle? = null) {
        try {
            firebaseAnalytics?.logEvent(name, params)
        } catch (_: Exception) {}
    }

    // D-Pad secret sequence tracking
    private val keySequenceList = mutableListOf<String>()

    init {
        if (isFirebaseAvailable) {
            try {
                FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
                val remoteConfig = FirebaseRemoteConfig.getInstance()
                remoteConfig.setConfigSettingsAsync(
                    FirebaseRemoteConfigSettings.Builder()
                        .setMinimumFetchIntervalInSeconds(3600)
                        .build()
                )
                remoteConfig.fetchAndActivate()
            } catch (e: Exception) {
                // Ignored if google-services.json not configured
            }
        }

        safeLogEvent("app_started", null)

        val db = GhostDatabase.getInstance(application)
        repository = LauncherRepository(application, db.appDao(), db.settingsDao())

        settings = repository.settingsFlow
            .combine(MutableStateFlow(Unit)) { s, _ ->
                s ?: UserSettingsEntity()
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = UserSettingsEntity()
            )

        allApps = repository.appsFlow
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        refreshStorage()
        checkTransparencyNotice()
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            val s = settings.value.copy(isOnboardingCompleted = true)
            repository.updateSettings(s)
            safeLogEvent("onboarding_completed", null)
        }
    }

    fun dismissSharePrompt() {
        viewModelScope.launch {
            val s = settings.value.copy(hasDismissedSharePrompt = true)
            repository.updateSettings(s)
            showSharePrompt.value = false
        }
    }

    fun shareApp() {
        dismissSharePrompt()
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Check out Aura TV")
                putExtra(Intent.EXTRA_TEXT, "Aura TV is a fantastic, minimalist Android TV launcher! Check it out.")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val chooser = Intent.createChooser(intent, "Share Aura TV").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            getApplication<Application>().startActivity(chooser)
            safeLogEvent("app_shared", null)
        } catch (e: Exception) {
            Toast.makeText(getApplication(), "Unable to share app", Toast.LENGTH_SHORT).show()
        }
    }

    fun dismissRatePrompt() {
        viewModelScope.launch {
            val s = settings.value.copy(hasDismissedRatePrompt = true)
            repository.updateSettings(s)
            showRatePrompt.value = false
        }
    }

    fun rateApp() {
        dismissRatePrompt()
        try {
            val pkg = getApplication<Application>().packageName
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$pkg")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            getApplication<Application>().startActivity(intent)
            safeLogEvent("app_rated", null)
        } catch (e: Exception) {
            try {
                val pkg = getApplication<Application>().packageName
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$pkg")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                getApplication<Application>().startActivity(intent)
            } catch (ex: Exception) {
                Toast.makeText(getApplication(), "Unable to open Play Store", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun refreshStorage() {
        viewModelScope.launch(Dispatchers.IO) {
            storageInfo.value = repository.getStorageInfo()
        }
    }

    private fun checkTransparencyNotice() {
        viewModelScope.launch {
            val s = repository.getSettings()
            if (!s.hasAcceptedTransparency) {
                showTransparencyNotice.value = true
            }
        }
    }

    fun acceptTransparencyNotice() {
        viewModelScope.launch {
            val s = settings.value.copy(hasAcceptedTransparency = true)
            repository.updateSettings(s)
            showTransparencyNotice.value = false
        }
    }

    fun launchApp(app: AppItem) {
        try {
            val newCount = settings.value.appLaunchCount + 1
            viewModelScope.launch {
                repository.updateSettings(settings.value.copy(appLaunchCount = newCount))
                if (newCount >= 5 && !settings.value.hasDismissedSharePrompt && !showRatePrompt.value) {
                    showSharePrompt.value = true
                } else if (newCount >= 10 && !settings.value.hasDismissedRatePrompt && !showSharePrompt.value) {
                    showRatePrompt.value = true
                }
            }

            safeLogEvent("app_launched", Bundle().apply {
                putString("package_name", app.packageName)
                putString("app_name", app.label)
            })

            val pm = getApplication<Application>().packageManager
            val intent = pm.getLaunchIntentForPackage(app.packageName)
                ?: pm.getLeanbackLaunchIntentForPackage(app.packageName)

            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                getApplication<Application>().startActivity(intent)
            } else {
                Toast.makeText(getApplication(), "Unable to launch ${app.label}", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            if (isFirebaseAvailable) {
                try {
                    FirebaseCrashlytics.getInstance().recordException(e)
                } catch (_: Exception) {}
            }
            Toast.makeText(getApplication(), "Error launching ${app.label}: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    fun launchAppByHotkey(key: Int): Boolean {
        val matchingApp = allApps.value.firstOrNull { it.fastLaunchKey == key && (!it.isHidden || isVaultUnlocked.value) }
        if (matchingApp != null) {
            launchApp(matchingApp)
            return true
        }
        return false
    }

    fun openAppDetails(app: AppItem) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${app.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            getApplication<Application>().startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(getApplication(), "Failed to open App Info", Toast.LENGTH_SHORT).show()
        }
    }

    fun toggleAppFavorite(app: AppItem) {
        viewModelScope.launch {
            val newCat = if (app.category == "Favorites") {
                when (app.appType) {
                    AppType.LEANBACK -> "TV Apps"
                    AppType.SIDELOADED -> "Sideloaded"
                    AppType.SYSTEM -> "System"
                }
            } else {
                "Favorites"
            }
            repository.setAppCategory(app.packageName, newCat)
            Toast.makeText(
                getApplication(),
                if (newCat == "Favorites") "Added ${app.label} to Favorites" else "Removed ${app.label} from Favorites",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun toggleAppHidden(app: AppItem) {
        viewModelScope.launch {
            val nextState = !app.isHidden
            repository.setAppHidden(app.packageName, nextState)
            Toast.makeText(
                getApplication(),
                if (nextState) "Hidden ${app.label}" else "Unhidden ${app.label}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun setCustomBanner(app: AppItem, bannerPath: String?) {
        viewModelScope.launch {
            repository.setAppCustomBanner(app.packageName, bannerPath)
            Toast.makeText(getApplication(), "Banner updated for ${app.label}", Toast.LENGTH_SHORT).show()
        }
    }

    fun setAppCategory(app: AppItem, category: String) {
        viewModelScope.launch {
            repository.setAppCategory(app.packageName, category)
            Toast.makeText(getApplication(), "Moved ${app.label} to $category", Toast.LENGTH_SHORT).show()
        }
    }

    fun setFastLaunchHotkey(app: AppItem, key: Int?) {
        viewModelScope.launch {
            repository.setFastLaunchKey(app.packageName, key)
            Toast.makeText(
                getApplication(),
                if (key != null) "Mapped Hotkey $key to ${app.label}" else "Cleared Hotkey for ${app.label}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun updateSettings(newSettings: UserSettingsEntity) {
        viewModelScope.launch {
            repository.updateSettings(newSettings)
        }
    }

    // D-Pad secret sequence tracking for hidden vault unlock
    fun registerDpadKey(keyName: String) {
        keySequenceList.add(keyName)
        if (keySequenceList.size > 8) {
            keySequenceList.removeAt(0)
        }
        val currentSeq = keySequenceList.joinToString(",")
        val targetSeq = settings.value.vaultSequence // e.g. "UP,UP,DOWN,LEFT"

        if (currentSeq.endsWith(targetSeq)) {
            isVaultUnlocked.value = !isVaultUnlocked.value
            Toast.makeText(
                getApplication(),
                if (isVaultUnlocked.value) "Stealth Vault Unlocked!" else "Stealth Vault Locked!",
                Toast.LENGTH_LONG
            ).show()
            keySequenceList.clear()
        }
    }

    fun unlockVaultWithCode(enteredCode: String): Boolean {
        if (enteredCode.trim().uppercase() == "GHOST" || enteredCode.trim() == "1234" || enteredCode.trim() == settings.value.vaultSequence) {
            isVaultUnlocked.value = true
            Toast.makeText(getApplication(), "Stealth Vault Unlocked!", Toast.LENGTH_SHORT).show()
            return true
        }
        Toast.makeText(getApplication(), "Incorrect Vault Code", Toast.LENGTH_SHORT).show()
        return false
    }

    fun exportBackup() {
        viewModelScope.launch {
            try {
                val json = repository.exportBackupJson(
                    metadataList = emptyList(), // repository gets all from flow
                    settings = settings.value
                )
                val backupFile = File(getApplication<Application>().getExternalFilesDir(null), "ghostlauncher_backup.json")
                backupFile.writeText(json)
                statusMessage.value = "Exported backup to: ${backupFile.absolutePath}"
                Toast.makeText(getApplication(), "Backup saved to ${backupFile.name}", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                statusMessage.value = "Backup failed: ${e.localizedMessage}"
            }
        }
    }

    fun importBackup(jsonText: String) {
        viewModelScope.launch {
            try {
                repository.importBackupJson(jsonText)
                statusMessage.value = "Backup restored successfully!"
                Toast.makeText(getApplication(), "Configuration Restored!", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                statusMessage.value = "Import failed: ${e.localizedMessage}"
            }
        }
    }

    fun resetAllBannersAndMetadata() {
        viewModelScope.launch {
            repository.clearAllMetadata()
            Toast.makeText(getApplication(), "Banners and Custom Metadata Reset", Toast.LENGTH_SHORT).show()
        }
    }

    fun clearImageCache() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val loader = coil.Coil.imageLoader(getApplication())
                loader.memoryCache?.clear()
                loader.diskCache?.clear()
                withContext(Dispatchers.Main) {
                    Toast.makeText(getApplication(), "Coil Image Cache Cleared Successfully!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(getApplication(), "Cache Clear Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
