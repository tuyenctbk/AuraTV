package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvGridItemSpan
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.TvOff
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import com.example.ui.components.OnboardingDialog
import com.example.ui.components.SuggestionDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.AppItem
import com.example.ui.components.AppCard
import com.example.ui.components.AppOptionDialog
import com.example.ui.components.CategoryMoveDialog
import com.example.ui.components.CustomBannerDialog
import com.example.ui.components.HeaderBar
import com.example.ui.components.HelpOverlayDialog
import com.example.ui.components.HiddenVaultDialog
import com.example.ui.components.HotkeyDialog
import com.example.ui.components.SettingsSidebar
import com.example.ui.components.TransparencyNoticeDialog
import com.example.ui.components.WallpaperBackground
import com.example.ui.theme.TvFocusGlow
import com.example.ui.theme.TvPrimary
import com.example.ui.theme.TvSurfaceVariant
import com.example.ui.viewmodel.LauncherViewModel

@Composable
fun MainLauncherScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val allApps by viewModel.allApps.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val isVaultUnlocked by viewModel.isVaultUnlocked.collectAsStateWithLifecycle()
    val storageInfo by viewModel.storageInfo.collectAsStateWithLifecycle()

    // Dialog state
    val selectedAppForOptions by viewModel.selectedAppForOptions.collectAsStateWithLifecycle()
    val showSettings by viewModel.showSettings.collectAsStateWithLifecycle()
    val showHelpOverlay by viewModel.showHelpOverlay.collectAsStateWithLifecycle()
    val showVaultUnlockModal by viewModel.showVaultUnlockModal.collectAsStateWithLifecycle()
    val showCustomBannerDialogForApp by viewModel.showCustomBannerDialogForApp.collectAsStateWithLifecycle()
    val showHotkeyDialogForApp by viewModel.showHotkeyDialogForApp.collectAsStateWithLifecycle()
    val showCategoryDialogForApp by viewModel.showCategoryDialogForApp.collectAsStateWithLifecycle()
    val showTransparencyNotice by viewModel.showTransparencyNotice.collectAsStateWithLifecycle()
    val showSharePrompt by viewModel.showSharePrompt.collectAsStateWithLifecycle()
    val showRatePrompt by viewModel.showRatePrompt.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                viewModel.searchQuery.value = spokenText
            }
        }
    }

    val launchVoiceSearch = remember {
        {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Search TV apps...")
            }
            try {
                speechRecognizerLauncher.launch(intent)
            } catch (e: Exception) {
                Toast.makeText(context, "Voice Search not available on this device", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Derive list of available categories
    val categoryList = remember(allApps, settings.categoriesOrder) {
        val baseCategories = listOf("All", "Favorites", "TV Apps", "Sideloaded", "Streaming", "Games", "System")
        val customFolders = settings.categoriesOrder.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        val appCategories = allApps.map { it.category }.distinct()
        (baseCategories + customFolders + appCategories).distinct()
    }

    // Category app counts
    val categoryCounts = remember(allApps, isVaultUnlocked) {
        val visibleApps = allApps.filter { !it.isHidden || isVaultUnlocked }
        val map = mutableMapOf<String, Int>()
        map["All"] = visibleApps.size
        map["Favorites"] = visibleApps.count { it.category == "Favorites" }
        map["TV Apps"] = visibleApps.count { it.category == "TV Apps" }
        map["Sideloaded"] = visibleApps.count { it.category == "Sideloaded" }
        visibleApps.forEach { app ->
            map[app.category] = (map[app.category] ?: 0) + 1
        }
        map
    }

    // Filter apps
    val filteredApps = remember(allApps, searchQuery, selectedCategory, isVaultUnlocked) {
        allApps.filter { app ->
            // Vault filter
            if (app.isHidden && !isVaultUnlocked) return@filter false

            // Category filter
            if (selectedCategory != "All" && app.category != selectedCategory) return@filter false

            // Search filter
            if (searchQuery.isNotBlank()) {
                val q = searchQuery.trim().lowercase()
                val matchName = app.label.lowercase().contains(q)
                val matchPkg = app.packageName.lowercase().contains(q)
                if (!matchName && !matchPkg) return@filter false
            }

            true
        }
    }

    // Group apps for category section headers
    val groupedApps = remember(filteredApps) {
        filteredApps.groupBy { it.category }
    }

    // OLED Screensaver & Inactivity State
    var lastActivityTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var isScreensaverActive by remember { mutableStateOf(false) }

    LaunchedEffect(settings.screensaverTimeoutMinutes, lastActivityTime, isScreensaverActive) {
        if (settings.screensaverTimeoutMinutes > 0 && !isScreensaverActive) {
            val timeoutMillis = settings.screensaverTimeoutMinutes * 60 * 1000L
            while (true) {
                kotlinx.coroutines.delay(3000L)
                val elapsed = System.currentTimeMillis() - lastActivityTime
                if (elapsed >= timeoutMillis) {
                    isScreensaverActive = true
                    break
                }
            }
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .onKeyEvent { keyEvent ->
                lastActivityTime = System.currentTimeMillis()
                if (isScreensaverActive) {
                    isScreensaverActive = false
                    return@onKeyEvent true
                }

                if (keyEvent.type == KeyEventType.KeyUp) {
                    val nativeKeyCode = keyEvent.nativeKeyEvent.keyCode
                    val isVoiceKey = nativeKeyCode == KeyEvent.KEYCODE_SEARCH ||
                            nativeKeyCode == KeyEvent.KEYCODE_VOICE_ASSIST ||
                            nativeKeyCode == KeyEvent.KEYCODE_ASSIST ||
                            nativeKeyCode == 143 // KEYCODE_MIC

                    if (isVoiceKey) {
                        launchVoiceSearch()
                        return@onKeyEvent true
                    }
                }

                if (keyEvent.key == Key.Guide || keyEvent.key == Key.Help) {
                    viewModel.showHelpOverlay.value = true
                    return@onKeyEvent true
                }

                // Remote Number Hotkeys (0-9)
                val hotkeyNumber = when (keyEvent.key) {
                    Key.Zero, Key.NumPad0 -> 0
                    Key.One, Key.NumPad1 -> 1
                    Key.Two, Key.NumPad2 -> 2
                    Key.Three, Key.NumPad3 -> 3
                    Key.Four, Key.NumPad4 -> 4
                    Key.Five, Key.NumPad5 -> 5
                    Key.Six, Key.NumPad6 -> 6
                    Key.Seven, Key.NumPad7 -> 7
                    Key.Eight, Key.NumPad8 -> 8
                    Key.Nine, Key.NumPad9 -> 9
                    else -> null
                }

                if (hotkeyNumber != null) {
                    viewModel.launchAppByHotkey(hotkeyNumber)
                } else {
                    // Secret D-Pad Sequence Registration
                    when (keyEvent.key) {
                        Key.DirectionUp -> viewModel.registerDpadKey("UP")
                        Key.DirectionDown -> viewModel.registerDpadKey("DOWN")
                        Key.DirectionLeft -> viewModel.registerDpadKey("LEFT")
                        Key.DirectionRight -> viewModel.registerDpadKey("RIGHT")
                    }
                    false
                }
            }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Wallpaper Background
            WallpaperBackground(
                wallpaperUri = settings.wallpaperUri,
                dimLevel = settings.wallpaperDimLevel
            )

            // Content Overlay
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header Bar (Clock, Storage, Search Bar, Settings)
                HeaderBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { viewModel.searchQuery.value = it },
                    storageInfo = storageInfo,
                    showStorageWidget = settings.showStorageWidget,
                    showClockWidget = settings.showClockWidget,
                    isVaultUnlocked = isVaultUnlocked,
                    onOpenVaultModal = { viewModel.showVaultUnlockModal.value = true },
                    onOpenSettings = { viewModel.showSettings.value = true },
                    onOpenHelp = { viewModel.showHelpOverlay.value = true },
                    onTriggerVoiceSearch = launchVoiceSearch
                )

                // Dynamic Category Filter Row
                CategoryFilterRow(
                    categories = categoryList,
                    categoryCounts = categoryCounts,
                    selectedCategory = selectedCategory,
                    onSelectCategory = { viewModel.selectedCategory.value = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // App Grid Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    if (filteredApps.isNotEmpty()) {
                        TvLazyVerticalGrid(
                            columns = TvGridCells.Fixed(settings.columnsCount.coerceIn(4, 7)),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("app_grid")
                        ) {
                            if (settings.showCategoryHeaders) {
                                groupedApps.forEach { (catName, appsInCat) ->
                                    item(span = { TvGridItemSpan(maxLineSpan) }, key = "header_$catName") {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 12.dp, bottom = 6.dp, start = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .width(4.dp)
                                                    .height(16.dp)
                                                    .clip(RoundedCornerShape(2.dp))
                                                    .background(TvPrimary)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = catName.uppercase(),
                                                color = Color.White.copy(alpha = 0.9f),
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 1.2.sp
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "(${appsInCat.size})",
                                                color = Color.White.copy(alpha = 0.4f),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                    items(
                                        items = appsInCat,
                                        key = { it.packageName }
                                    ) { app ->
                                        AppCard(
                                            app = app,
                                            cardRatio = settings.cardRatio,
                                            showLabels = settings.showLabels,
                                            reduceMotion = settings.reduceMotion,
                                            onLaunch = { viewModel.launchApp(it) },
                                            onOptions = { viewModel.selectedAppForOptions.value = it }
                                        )
                                    }
                                }
                            } else {
                                items(
                                    items = filteredApps,
                                    key = { it.packageName }
                                ) { app ->
                                    AppCard(
                                        app = app,
                                        cardRatio = settings.cardRatio,
                                        showLabels = settings.showLabels,
                                        reduceMotion = settings.reduceMotion,
                                        onLaunch = { viewModel.launchApp(it) },
                                        onOptions = { viewModel.selectedAppForOptions.value = it }
                                    )
                                }
                            }
                        }
                    } else {
                        // Empty State
                        EmptyStateView(
                            searchQuery = searchQuery,
                            selectedCategory = selectedCategory,
                            isVaultUnlocked = isVaultUnlocked
                        )
                    }
                }

                // Remote Control Shortcuts Hint Bar
                RemoteControlShortcutsBar()
            }

            // Dialogs
            selectedAppForOptions?.let { app ->
                AppOptionDialog(
                    app = app,
                    onDismiss = { viewModel.selectedAppForOptions.value = null },
                    onLaunch = { viewModel.launchApp(it) },
                    onToggleFavorite = { viewModel.toggleAppFavorite(it) },
                    onDeepStopInfo = { viewModel.openAppDetails(it) },
                    onOpenCustomBanner = { viewModel.showCustomBannerDialogForApp.value = it },
                    onOpenHotkeyAssign = { viewModel.showHotkeyDialogForApp.value = it },
                    onOpenCategoryMove = { viewModel.showCategoryDialogForApp.value = it },
                    onToggleHide = { viewModel.toggleAppHidden(it) }
                )
            }

            showCustomBannerDialogForApp?.let { app ->
                CustomBannerDialog(
                    app = app,
                    onDismiss = { viewModel.showCustomBannerDialogForApp.value = null },
                    onSaveBannerPath = { bannerPath ->
                        viewModel.setCustomBanner(app, bannerPath)
                    }
                )
            }

            showHotkeyDialogForApp?.let { app ->
                HotkeyDialog(
                    app = app,
                    onDismiss = { viewModel.showHotkeyDialogForApp.value = null },
                    onSetHotkey = { key ->
                        viewModel.setFastLaunchHotkey(app, key)
                    }
                )
            }

            showCategoryDialogForApp?.let { app ->
                CategoryMoveDialog(
                    app = app,
                    existingCategories = categoryList,
                    onDismiss = { viewModel.showCategoryDialogForApp.value = null },
                    onMoveCategory = { newCategory ->
                        viewModel.setAppCategory(app, newCategory)
                    }
                )
            }

            if (showVaultUnlockModal) {
                HiddenVaultDialog(
                    onDismiss = { viewModel.showVaultUnlockModal.value = false },
                    onUnlockWithCode = { code ->
                        viewModel.unlockVaultWithCode(code)
                    }
                )
            }

            if (showSettings) {
                SettingsSidebar(
                    settings = settings,
                    onUpdateSettings = { viewModel.updateSettings(it) },
                    onDismiss = { viewModel.showSettings.value = false },
                    onExportBackup = { viewModel.exportBackup() },
                    onResetAllBanners = { viewModel.resetAllBannersAndMetadata() },
                    onToggleVaultUnlock = { viewModel.isVaultUnlocked.value = !isVaultUnlocked },
                    onClearCache = { viewModel.clearImageCache() },
                    onOpenHelp = { viewModel.showHelpOverlay.value = true }
                )
            }

            if (showHelpOverlay) {
                HelpOverlayDialog(
                    onDismiss = { viewModel.showHelpOverlay.value = false }
                )
            }

            if (showTransparencyNotice) {
                TransparencyNoticeDialog(
                    onAccept = { viewModel.acceptTransparencyNotice() }
                )
            }

            if (isScreensaverActive) {
                OledScreensaverOverlay(
                    onDismiss = {
                        isScreensaverActive = false
                        lastActivityTime = System.currentTimeMillis()
                    }
                )
            }

            if (!settings.isOnboardingCompleted) {
                OnboardingDialog(
                    onComplete = { viewModel.completeOnboarding() }
                )
            }

            if (showSharePrompt) {
                SuggestionDialog(
                    icon = Icons.Default.Share,
                    title = stringResource(R.string.share_title),
                    description = stringResource(R.string.share_desc),
                    primaryButtonText = stringResource(R.string.share_action),
                    onPrimary = { viewModel.shareApp() },
                    onDismiss = { viewModel.dismissSharePrompt() }
                )
            }

            if (showRatePrompt) {
                SuggestionDialog(
                    icon = Icons.Default.Star,
                    title = stringResource(R.string.rate_title),
                    description = stringResource(R.string.rate_desc),
                    primaryButtonText = stringResource(R.string.rate_action),
                    onPrimary = { viewModel.rateApp() },
                    onDismiss = { viewModel.dismissRatePrompt() }
                )
            }
        }
    }
}

@Composable
private fun CategoryFilterRow(
    categories: List<String>,
    categoryCounts: Map<String, Int> = emptyMap(),
    selectedCategory: String,
    onSelectCategory: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(categories) { catName ->
            val isSelected = catName == selectedCategory
            var isFocused by remember { mutableStateOf(false) }
            val count = categoryCounts[catName] ?: 0

            val iconVector = when (catName) {
                "All" -> Icons.Default.Apps
                "Favorites" -> Icons.Default.Star
                "TV Apps" -> Icons.Default.Tv
                "Sideloaded" -> Icons.Default.Android
                "Streaming" -> Icons.Default.PlayArrow
                else -> Icons.Default.Folder
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        when {
                            isFocused -> TvFocusGlow
                            isSelected -> TvPrimary
                            else -> TvSurfaceVariant.copy(alpha = 0.6f)
                        }
                    )
                    .onFocusChanged { isFocused = it.isFocused }
                    .focusable()
                    .clickable { onSelectCategory(catName) }
                    .padding(horizontal = 14.dp, vertical = 7.dp)
                    .testTag("category_chip_$catName"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = catName,
                        tint = if (isFocused || isSelected) Color.Black else (if (catName == "Favorites") Color(0xFFF59E0B) else Color.White.copy(alpha = 0.9f)),
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = catName,
                        color = if (isFocused || isSelected) Color.Black else Color.White,
                        fontWeight = if (isSelected || isFocused) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.sp
                    )

                    if (count > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isFocused || isSelected) Color.Black.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.15f)
                                )
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = count.toString(),
                                color = if (isFocused || isSelected) Color.Black else Color.White.copy(alpha = 0.8f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RemoteControlShortcutsBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ShortcutHintPill(keyLabel = "OK", actionText = "Launch")
            ShortcutHintPill(keyLabel = "HOLD OK / MENU", actionText = "Options")
            ShortcutHintPill(keyLabel = "0-9", actionText = "Hotkey")
            ShortcutHintPill(keyLabel = "MIC", actionText = "Voice")
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "AURA TV",
                color = Color.White.copy(alpha = 0.35f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
        }
    }
}

@Composable
private fun ShortcutHintPill(keyLabel: String, actionText: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color.White.copy(alpha = 0.12f))
                .padding(horizontal = 5.dp, vertical = 2.dp)
        ) {
            Text(
                text = keyLabel,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = actionText,
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun EmptyStateView(
    searchQuery: String,
    selectedCategory: String,
    isVaultUnlocked: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.TvOff,
                contentDescription = "Empty",
                tint = TvFocusGlow.copy(alpha = 0.6f),
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (searchQuery.isNotBlank()) "No apps found matching \"$searchQuery\"" else "No applications in \"$selectedCategory\"",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (!isVaultUnlocked) "Tip: Some apps may be hidden in the Stealth Vault." else "Try selecting another category folder or clear search.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun OledScreensaverOverlay(
    onDismiss: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    var currentTime by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        val timeFmt = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
        val dateFmt = java.text.SimpleDateFormat("EEEE, MMMM d, yyyy", java.util.Locale.getDefault())
        while (true) {
            val now = java.util.Date()
            currentTime = timeFmt.format(now)
            currentDate = dateFmt.format(now)
            kotlinx.coroutines.delay(1000L)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "oledMotion")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = -25f,
        targetValue = 25f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offsetY"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent {
                onDismiss()
                true
            }
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.graphicsLayer { translationY = offsetY },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.35f,
                targetValue = 0.85f,
                animationSpec = infiniteRepeatable(
                    animation = tween(3500, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "screensaverAlpha"
            )

            Text(
                text = currentTime.ifEmpty { "00:00:00" },
                color = Color.White.copy(alpha = alpha),
                fontSize = 56.sp,
                fontWeight = FontWeight.ExtraLight,
                letterSpacing = 2.sp
            )

            if (currentDate.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = currentDate,
                    color = Color.White.copy(alpha = 0.45f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "AURA TV",
                color = TvPrimary.copy(alpha = alpha * 0.9f),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 6.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "OLED Ambient Saver — Press any button to resume",
                color = Color.White.copy(alpha = 0.3f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}
