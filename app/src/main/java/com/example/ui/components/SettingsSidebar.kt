package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.example.util.MediaPickerHelper
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.db.UserSettingsEntity
import com.example.ui.theme.TvFocusGlow
import com.example.ui.theme.TvPrimary
import com.example.ui.theme.TvSurfaceVariant

@Composable
fun SettingsSidebar(
    settings: UserSettingsEntity,
    onUpdateSettings: (UserSettingsEntity) -> Unit,
    onDismiss: () -> Unit,
    onExportBackup: () -> Unit,
    onResetAllBanners: () -> Unit,
    onToggleVaultUnlock: () -> Unit,
    onClearCache: (() -> Unit)? = null,
    onOpenHelp: (() -> Unit)? = null,
    onOpenAppVisibilityManager: (() -> Unit)? = null,
    onOpenFolderManager: (() -> Unit)? = null
) {
    var versionClickCount by remember { mutableIntStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            coroutineScope.launch {
                val localPath = MediaPickerHelper.savePickedUriLocally(
                    context = context,
                    uri = uri,
                    subfolder = "wallpapers",
                    prefix = "wallpaper"
                )
                if (!localPath.isNullOrEmpty()) {
                    onUpdateSettings(settings.copy(wallpaperUri = localPath))
                }
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp),
            color = Color(0xFF0F172A),
            modifier = Modifier
                .width(420.dp)
                .fillMaxHeight()
                .border(1.dp, TvFocusGlow.copy(alpha = 0.5f), RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp))
                .testTag("settings_sidebar")
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Top Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings_title),
                            tint = TvPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = stringResource(R.string.settings_title),
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    var isCloseFocused by remember { mutableStateOf(false) }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isCloseFocused) TvFocusGlow else TvSurfaceVariant)
                            .onFocusChanged { isCloseFocused = it.isFocused }
                            .focusable()
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.close),
                            tint = if (isCloseFocused) Color.Black else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Section 1: UI Layout
                SettingsSectionHeader(icon = Icons.Default.Tv, title = stringResource(R.string.section_grid_display))

                Spacer(modifier = Modifier.height(8.dp))

                // Columns Selector
                Text(
                    text = stringResource(R.string.grid_columns_format, settings.columnsCount),
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    (4..7).forEach { cols ->
                        val isSelected = settings.columnsCount == cols
                        var isBtnFocused by remember { mutableStateOf(false) }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isBtnFocused) TvFocusGlow else (if (isSelected) TvPrimary else TvSurfaceVariant))
                            .onFocusChanged { isBtnFocused = it.isFocused }
                            .focusable()
                            .clickable {
                                onUpdateSettings(settings.copy(columnsCount = cols))
                            },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.cols_format, cols),
                                color = if (isBtnFocused || isSelected) Color.Black else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Card Ratio
                Text(
                    text = stringResource(R.string.card_aspect_ratio),
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("16:9", "1:1").forEach { ratio ->
                        val isSelected = settings.cardRatio == ratio
                        var isBtnFocused by remember { mutableStateOf(false) }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isBtnFocused) TvFocusGlow else (if (isSelected) TvPrimary else TvSurfaceVariant))
                            .onFocusChanged { isBtnFocused = it.isFocused }
                            .focusable()
                            .clickable {
                                onUpdateSettings(settings.copy(cardRatio = ratio))
                            },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (ratio == "16:9") stringResource(R.string.aspect_ratio_16_9) else stringResource(R.string.aspect_ratio_1_1),
                                color = if (isBtnFocused || isSelected) Color.Black else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Toggles
                SettingsToggleRow(
                    title = stringResource(R.string.show_app_labels),
                    checked = settings.showLabels,
                    onCheckedChange = { onUpdateSettings(settings.copy(showLabels = it)) }
                )

                SettingsToggleRow(
                    title = stringResource(R.string.reduce_motion_desc),
                    checked = settings.reduceMotion,
                    onCheckedChange = { onUpdateSettings(settings.copy(reduceMotion = it)) }
                )

                SettingsToggleRow(
                    title = stringResource(R.string.show_storage_desc),
                    checked = settings.showStorageWidget,
                    onCheckedChange = { onUpdateSettings(settings.copy(showStorageWidget = it)) }
                )

                SettingsToggleRow(
                    title = stringResource(R.string.show_clock_desc),
                    checked = settings.showClockWidget,
                    onCheckedChange = { onUpdateSettings(settings.copy(showClockWidget = it)) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // App Grid Sorting Order
                Text(
                    text = stringResource(R.string.app_sort_order),
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "ALPHABETICAL" to stringResource(R.string.sort_alphabetical_short),
                        "RECENTLY_USED" to stringResource(R.string.sort_recent_short),
                        "FREQUENTLY_USED" to stringResource(R.string.sort_frequent_short),
                        "INSTALL_DATE" to stringResource(R.string.sort_newest_short)
                    ).forEach { (sortKey, sortLabel) ->
                        val isSelected = settings.sortOrder == sortKey
                        var isSortFocused by remember { mutableStateOf(false) }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSortFocused) TvFocusGlow else (if (isSelected) TvPrimary else TvSurfaceVariant))
                            .onFocusChanged { isSortFocused = it.isFocused }
                            .focusable()
                            .clickable {
                                onUpdateSettings(settings.copy(sortOrder = sortKey))
                            },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = sortLabel,
                                color = if (isSortFocused || isSelected) Color.Black else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // App Visibility & Folder Shortcuts
                if (onOpenAppVisibilityManager != null || onOpenFolderManager != null) {
                    Text(
                        text = stringResource(R.string.app_folder_org),
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    if (onOpenAppVisibilityManager != null) {
                        var isVisBtnFocused by remember { mutableStateOf(false) }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isVisBtnFocused) TvFocusGlow else TvSurfaceVariant.copy(alpha = 0.6f))
                                .border(1.dp, if (isVisBtnFocused) TvFocusGlow else Color.Transparent, RoundedCornerShape(10.dp))
                                .onFocusChanged { isVisBtnFocused = it.isFocused }
                                .focusable()
                                .clickable { onOpenAppVisibilityManager() }
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Tv,
                                    contentDescription = null,
                                    tint = if (isVisBtnFocused) Color.Black else TvPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = stringResource(R.string.manage_app_visibility),
                                        color = if (isVisBtnFocused) Color.Black else Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = stringResource(R.string.toggle_apps_home),
                                        color = if (isVisBtnFocused) Color.Black.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.5f),
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    if (onOpenFolderManager != null) {
                        var isFldBtnFocused by remember { mutableStateOf(false) }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isFldBtnFocused) TvFocusGlow else TvSurfaceVariant.copy(alpha = 0.6f))
                                .border(1.dp, if (isFldBtnFocused) TvFocusGlow else Color.Transparent, RoundedCornerShape(10.dp))
                                .onFocusChanged { isFldBtnFocused = it.isFocused }
                                .focusable()
                                .clickable { onOpenFolderManager() }
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Folder,
                                    contentDescription = null,
                                    tint = if (isFldBtnFocused) Color.Black else TvPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = stringResource(R.string.organize_folders),
                                        color = if (isFldBtnFocused) Color.Black else Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = stringResource(R.string.organize_folders_desc),
                                        color = if (isFldBtnFocused) Color.Black.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.5f),
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = stringResource(R.string.screensaver_label),
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = stringResource(R.string.screensaver_desc),
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 10.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                val screensaverOptions = listOf(
                    0 to stringResource(R.string.screensaver_off),
                    1 to stringResource(R.string.screensaver_min, 1),
                    3 to stringResource(R.string.screensaver_min, 3),
                    5 to stringResource(R.string.screensaver_min, 5),
                    10 to stringResource(R.string.screensaver_min, 10),
                    15 to stringResource(R.string.screensaver_min, 15)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    screensaverOptions.forEach { (mins, label) ->
                        val isSelected = settings.screensaverTimeoutMinutes == mins
                        var isOptFocused by remember { mutableStateOf(false) }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) TvPrimary else (if (isOptFocused) TvFocusGlow else TvSurfaceVariant))
                            .onFocusChanged { isOptFocused = it.isFocused }
                            .focusable()
                            .clickable {
                                onUpdateSettings(settings.copy(screensaverTimeoutMinutes = mins))
                            }
                            .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected || isOptFocused) Color.Black else Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = Color.White.copy(alpha = 0.1f)
                )

                // Section 2: Wallpaper Engine
                SettingsSectionHeader(icon = Icons.Default.Palette, title = stringResource(R.string.section_wallpaper_engine))

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.preset_themes),
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "preset_dark_cyber" to stringResource(R.string.preset_dark_cyber),
                        "preset_neon_aurora" to stringResource(R.string.preset_neon_aurora),
                        "preset_cyber_grid" to stringResource(R.string.preset_cyber_grid)
                    ).forEach { (presetKey, presetLabel) ->
                        val isSelected = settings.wallpaperUri == presetKey
                        var isBtnFocused by remember { mutableStateOf(false) }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isBtnFocused) TvFocusGlow else (if (isSelected) TvPrimary else TvSurfaceVariant))
                                .onFocusChanged { isBtnFocused = it.isFocused }
                                .focusable()
                                .clickable {
                                    onUpdateSettings(settings.copy(wallpaperUri = presetKey))
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = presetLabel,
                                color = if (isBtnFocused || isSelected) Color.Black else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                var isPickerFocused by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isPickerFocused) TvFocusGlow else TvPrimary)
                        .border(1.dp, if (isPickerFocused) Color.White else Color.Transparent, RoundedCornerShape(8.dp))
                        .onFocusChanged { isPickerFocused = it.isFocused }
                        .focusable()
                        .clickable {
                            try {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            } catch (e: Exception) {
                                Toast.makeText(context, context.getString(R.string.picker_unavailable), Toast.LENGTH_SHORT).show()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.choose_photo_picker),
                            color = Color.Black,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Custom 4K Wallpaper File Path Input
                var customWallpaperInput by remember(settings.wallpaperUri) {
                    mutableStateOf(if (settings.wallpaperUri?.startsWith("preset_") == false) settings.wallpaperUri.orEmpty() else "")
                }
                var isWpInputFocused by remember { mutableStateOf(false) }

                Text(
                    text = stringResource(R.string.custom_wallpaper_path),
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isWpInputFocused) TvSurfaceVariant else Color.White.copy(alpha = 0.1f))
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.foundation.text.BasicTextField(
                        value = customWallpaperInput,
                        onValueChange = { customWallpaperInput = it },
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 12.sp),
                        cursorBrush = androidx.compose.ui.graphics.SolidColor(TvFocusGlow),
                        modifier = Modifier
                            .weight(1f)
                            .onFocusChanged { isWpInputFocused = it.isFocused }
                            .focusable(),
                        decorationBox = { innerTextField ->
                            if (customWallpaperInput.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.custom_wallpaper_hint),
                                    color = Color.White.copy(alpha = 0.4f),
                                    fontSize = 11.sp
                                )
                            }
                            innerTextField()
                        }
                    )

                    if (customWallpaperInput.isNotBlank()) {
                        var isApplyFocused by remember { mutableStateOf(false) }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isApplyFocused) TvFocusGlow else TvPrimary)
                                .onFocusChanged { isApplyFocused = it.isFocused }
                                .focusable()
                                .clickable {
                                    onUpdateSettings(settings.copy(wallpaperUri = customWallpaperInput.trim()))
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.apply),
                                color = Color.Black,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = stringResource(R.string.brightness_dimmer_format, (settings.wallpaperDimLevel * 100).toInt()),
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = stringResource(R.string.dimmer_desc),
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 10.sp
                )

                Slider(
                    value = settings.wallpaperDimLevel,
                    onValueChange = { onUpdateSettings(settings.copy(wallpaperDimLevel = it)) },
                    valueRange = 0f..0.9f,
                    colors = SliderDefaults.colors(
                        thumbColor = TvFocusGlow,
                        activeTrackColor = TvPrimary,
                        inactiveTrackColor = TvSurfaceVariant
                    )
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = Color.White.copy(alpha = 0.1f)
                )

                // Section 3: Accent Color & Focus Glow System
                SettingsSectionHeader(icon = Icons.Default.ColorLens, title = stringResource(R.string.section_accent_glow))

                Spacer(modifier = Modifier.height(8.dp))

                SettingsToggleRow(
                    title = stringResource(R.string.extract_accent_title),
                    subtitle = stringResource(R.string.extract_accent_desc),
                    checked = settings.usePaletteExtraction,
                    onCheckedChange = { onUpdateSettings(settings.copy(usePaletteExtraction = it)) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = stringResource(R.string.global_accent_theme),
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = if (settings.usePaletteExtraction) stringResource(R.string.accent_fallback_desc) else stringResource(R.string.accent_primary_desc),
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 10.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                val accentSwatches = listOf(
                    "#D0BCFF" to stringResource(R.string.color_lavender),
                    "#00E5FF" to stringResource(R.string.color_cyan),
                    "#00E676" to stringResource(R.string.color_emerald),
                    "#FFD700" to stringResource(R.string.color_gold),
                    "#FF1744" to stringResource(R.string.color_crimson),
                    "#FF4081" to stringResource(R.string.color_pink),
                    "#9C27B0" to stringResource(R.string.color_purple)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    accentSwatches.forEach { (hex, name) ->
                        val swatchColor = try { Color(android.graphics.Color.parseColor(hex)) } catch (e: Exception) { TvFocusGlow }
                        val isSelected = settings.accentColorHex.equals(hex, ignoreCase = true)
                        var isSwatchFocused by remember { mutableStateOf(false) }

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(swatchColor)
                                .border(
                                    width = if (isSelected || isSwatchFocused) 3.dp else 1.dp,
                                    color = if (isSelected || isSwatchFocused) Color.White else Color.Black.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .onFocusChanged { isSwatchFocused = it.isFocused }
                                .focusable()
                                .clickable {
                                    onUpdateSettings(settings.copy(accentColorHex = hex))
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.White)
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = Color.White.copy(alpha = 0.1f)
                )

                // Section 4: Custom Folders & Categories
                SettingsSectionHeader(icon = Icons.Default.Folder, title = stringResource(R.string.section_custom_folders))

                Spacer(modifier = Modifier.height(8.dp))

                SettingsToggleRow(
                    title = stringResource(R.string.show_category_headers_title),
                    subtitle = stringResource(R.string.show_category_headers_desc),
                    checked = settings.showCategoryHeaders,
                    onCheckedChange = { onUpdateSettings(settings.copy(showCategoryHeaders = it)) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                var newFolderName by remember { mutableStateOf("") }
                var isFolderInputFocused by remember { mutableStateOf(false) }

                Text(
                    text = stringResource(R.string.create_custom_folder),
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isFolderInputFocused) TvSurfaceVariant else Color.White.copy(alpha = 0.1f))
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.foundation.text.BasicTextField(
                        value = newFolderName,
                        onValueChange = { newFolderName = it },
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 12.sp),
                        cursorBrush = androidx.compose.ui.graphics.SolidColor(TvFocusGlow),
                        modifier = Modifier
                            .weight(1f)
                            .onFocusChanged { isFolderInputFocused = it.isFocused }
                            .focusable(),
                        decorationBox = { innerTextField ->
                            if (newFolderName.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.folder_name_hint),
                                    color = Color.White.copy(alpha = 0.4f),
                                    fontSize = 11.sp
                                )
                            }
                            innerTextField()
                        }
                    )

                    if (newFolderName.isNotBlank()) {
                        var isAddFolderFocused by remember { mutableStateOf(false) }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isAddFolderFocused) TvFocusGlow else TvPrimary)
                                .onFocusChanged { isAddFolderFocused = it.isFocused }
                                .focusable()
                                .clickable {
                                    val currentOrders = settings.categoriesOrder.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                                    val updatedOrders = (currentOrders + newFolderName.trim()).distinct().joinToString(",")
                                    onUpdateSettings(settings.copy(categoriesOrder = updatedOrders))
                                    newFolderName = ""
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.add_folder),
                                color = Color.Black,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                val existingFolderList = remember(settings.categoriesOrder) {
                    settings.categoriesOrder.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                }

                if (existingFolderList.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.active_folders_format, existingFolderList.joinToString(", ")),
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = Color.White.copy(alpha = 0.1f)
                )

                // Section 5: Storage Cleanup Utility
                SettingsSectionHeader(icon = Icons.Default.CleaningServices, title = stringResource(R.string.section_storage_cleanup))

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.image_cache_cleaner_title),
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = stringResource(R.string.image_cache_cleaner_desc),
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                var isClearCacheFocused by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isClearCacheFocused) TvFocusGlow else TvSurfaceVariant)
                        .onFocusChanged { isClearCacheFocused = it.isFocused }
                        .focusable()
                        .clickable { onClearCache?.invoke() }
                        .padding(vertical = 11.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CleaningServices,
                            contentDescription = stringResource(R.string.clear_cache),
                            tint = if (isClearCacheFocused) Color.Black else TvPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.clear_coil_cache_btn),
                            color = if (isClearCacheFocused) Color.Black else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = Color.White.copy(alpha = 0.1f)
                )

                // Section 6: Backup & Help
                SettingsSectionHeader(icon = Icons.Default.Save, title = stringResource(R.string.section_backup_help))

                Spacer(modifier = Modifier.height(8.dp))

                var isExportFocused by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isExportFocused) TvFocusGlow else TvSurfaceVariant)
                        .onFocusChanged { isExportFocused = it.isFocused }
                        .focusable()
                        .clickable { onExportBackup() }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.export_backup_btn),
                        color = if (isExportFocused) Color.Black else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                var isResetFocused by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isResetFocused) TvFocusGlow else TvSurfaceVariant)
                        .onFocusChanged { isResetFocused = it.isFocused }
                        .focusable()
                        .clickable { onResetAllBanners() }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.reset_all_banners_btn),
                        color = if (isResetFocused) Color.Black else MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                var isHelpBtnFocused by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isHelpBtnFocused) TvFocusGlow else TvSurfaceVariant)
                        .onFocusChanged { isHelpBtnFocused = it.isFocused }
                        .focusable()
                        .clickable { onOpenHelp?.invoke() }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = stringResource(R.string.remote_guide),
                            tint = if (isHelpBtnFocused) Color.Black else TvPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.remote_shortcuts_legend_btn),
                            color = if (isHelpBtnFocused) Color.Black else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = Color.White.copy(alpha = 0.1f)
                )

                // Section 7: About Aura TV
                SettingsSectionHeader(icon = Icons.Default.Shield, title = stringResource(R.string.section_about))

                Spacer(modifier = Modifier.height(6.dp))

                var isVersionFocused by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isVersionFocused) TvFocusGlow else Color.Transparent)
                        .onFocusChanged { isVersionFocused = it.isFocused }
                        .focusable()
                        .clickable {
                            versionClickCount++
                            if (versionClickCount >= 5) {
                                versionClickCount = 0
                                onToggleVaultUnlock()
                            }
                        }
                        .padding(vertical = 6.dp)
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.about_app_title, "1.3"),
                            color = if (isVersionFocused) Color.Black else Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(R.string.about_app_desc),
                            color = if (isVersionFocused) Color.Black.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.5f),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = TvFocusGlow,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            color = TvFocusGlow,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    subtitle: String? = null
) {
    var isFocused by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isFocused) TvSurfaceVariant else Color.Transparent)
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            if (!subtitle.isNullOrEmpty()) {
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 10.sp
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = TvFocusGlow,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = TvSurfaceVariant
            )
        )
    }
}
