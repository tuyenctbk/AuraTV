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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.HelpOutline
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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
    onOpenHelp: (() -> Unit)? = null
) {
    var versionClickCount by remember { mutableIntStateOf(0) }

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
                            contentDescription = "Settings",
                            tint = TvPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Launcher Settings",
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
                            contentDescription = "Close",
                            tint = if (isCloseFocused) Color.Black else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Section 1: UI Layout
                SettingsSectionHeader(icon = Icons.Default.Tv, title = "Grid & Display Layout")

                Spacer(modifier = Modifier.height(8.dp))

                // Columns Selector
                Text(
                    text = "Grid Columns: ${settings.columnsCount}",
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
                                text = "$cols Cols",
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
                    text = "Card Aspect Ratio",
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
                                text = if (ratio == "16:9") "16:9 TV Banner" else "1:1 Mobile Square",
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
                    title = "Show App Labels",
                    checked = settings.showLabels,
                    onCheckedChange = { onUpdateSettings(settings.copy(showLabels = it)) }
                )

                SettingsToggleRow(
                    title = "Reduce Motion (Low-RAM Performance)",
                    checked = settings.reduceMotion,
                    onCheckedChange = { onUpdateSettings(settings.copy(reduceMotion = it)) }
                )

                SettingsToggleRow(
                    title = "Show Free Storage Bar",
                    checked = settings.showStorageWidget,
                    onCheckedChange = { onUpdateSettings(settings.copy(showStorageWidget = it)) }
                )

                SettingsToggleRow(
                    title = "Show Digital Clock",
                    checked = settings.showClockWidget,
                    onCheckedChange = { onUpdateSettings(settings.copy(showClockWidget = it)) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "OLED Screensaver Inactivity Timeout",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Triggers an OLED-safe ambient black screen after inactivity to prevent display burn-in.",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 10.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                val screensaverOptions = remember {
                    listOf(0 to "Off", 1 to "1 Min", 3 to "3 Min", 5 to "5 Min", 10 to "10 Min", 15 to "15 Min")
                }
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
                SettingsSectionHeader(icon = Icons.Default.Palette, title = "4K Wallpaper Engine & Dimmer")

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Preset Themes",
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
                        "preset_dark_cyber" to "Dark Cyber",
                        "preset_neon_aurora" to "Aurora",
                        "preset_cyber_grid" to "Deep Slate"
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

                Spacer(modifier = Modifier.height(8.dp))

                // Custom 4K Wallpaper File Path Input
                var customWallpaperInput by remember(settings.wallpaperUri) {
                    mutableStateOf(if (settings.wallpaperUri?.startsWith("preset_") == false) settings.wallpaperUri.orEmpty() else "")
                }
                var isWpInputFocused by remember { mutableStateOf(false) }

                Text(
                    text = "Custom Local 4K Image Path / URI",
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
                                    text = "e.g. /sdcard/Pictures/wallpaper.jpg or https://...",
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
                                text = "Apply",
                                color = Color.Black,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Background Brightness Dimmer: ${(settings.wallpaperDimLevel * 100).toInt()}%",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Higher dimming improves contrast & app label legibility over bright 4K wallpapers.",
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
                SettingsSectionHeader(icon = Icons.Default.ColorLens, title = "Accent Color & Glow Effect")

                Spacer(modifier = Modifier.height(8.dp))

                SettingsToggleRow(
                    title = "Extract Accent from App Icon (Palette API)",
                    subtitle = "Automatically extracts dynamic vibrant border glow colors from focused app icons.",
                    checked = settings.usePaletteExtraction,
                    onCheckedChange = { onUpdateSettings(settings.copy(usePaletteExtraction = it)) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Global Accent Theme Swatch",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = if (settings.usePaletteExtraction) "Used as fallback when icon palette is missing" else "Used as primary focus border glow color for all app cards",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 10.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                val accentSwatches = remember {
                    listOf(
                        "#D0BCFF" to "Lavender",
                        "#00E5FF" to "Cyan",
                        "#00E676" to "Emerald",
                        "#FFD700" to "Gold",
                        "#FF1744" to "Crimson",
                        "#FF4081" to "Pink",
                        "#9C27B0" to "Purple"
                    )
                }

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
                SettingsSectionHeader(icon = Icons.Default.Folder, title = "Custom Folders & Categories")

                Spacer(modifier = Modifier.height(8.dp))

                SettingsToggleRow(
                    title = "Show Category Section Headers in Grid",
                    subtitle = "Displays text section headers above app groups (e.g. 'Streaming', 'Games') in the app grid.",
                    checked = settings.showCategoryHeaders,
                    onCheckedChange = { onUpdateSettings(settings.copy(showCategoryHeaders = it)) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                var newFolderName by remember { mutableStateOf("") }
                var isFolderInputFocused by remember { mutableStateOf(false) }

                Text(
                    text = "Create Custom Folder (e.g. Streaming, Games)",
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
                                    text = "Folder Name (e.g. Anime, Utilities)...",
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
                                text = "Add Folder",
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
                        text = "Active Folders: ${existingFolderList.joinToString(", ")}",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = Color.White.copy(alpha = 0.1f)
                )

                // Section 5: Storage Cleanup Utility
                SettingsSectionHeader(icon = Icons.Default.CleaningServices, title = "Storage Cleanup Utility")

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Coil Image Cache Storage Cleaner",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Purges cached icon & banner disk files to prevent the launcher from consuming excessive space on low-storage TV devices.",
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
                            contentDescription = "Clear Cache",
                            tint = if (isClearCacheFocused) Color.Black else TvPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Execute Storage Cleanup (Clear Coil Cache)",
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
                SettingsSectionHeader(icon = Icons.Default.Save, title = "Backup & Remote Help")

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
                        text = "Export Backup JSON Config",
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
                        text = "Reset All Custom Banners & Categories",
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
                            contentDescription = "Help Guide",
                            tint = if (isHelpBtnFocused) Color.Black else TvPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "TV Remote Shortcuts & Legend",
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
                SettingsSectionHeader(icon = Icons.Default.Shield, title = "About Aura TV")

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
                            text = "GhostLauncher v1.0 • Open Source TV Launcher",
                            color = if (isVersionFocused) Color.Black else Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "High-performance sideload manager for Android TV. Lightweight & Ad-free.",
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
