package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.drawable.toBitmap
import com.example.R
import com.example.data.model.AppItem
import com.example.ui.theme.TvFocusGlow
import com.example.ui.theme.TvPrimary
import com.example.ui.theme.TvSurfaceVariant
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AppOptionDialog(
    app: AppItem,
    onDismiss: () -> Unit,
    onLaunch: (AppItem) -> Unit,
    onToggleFavorite: (AppItem) -> Unit,
    onDeepStopInfo: (AppItem) -> Unit,
    onOpenCustomBanner: (AppItem) -> Unit,
    onOpenHotkeyAssign: (AppItem) -> Unit,
    onOpenCategoryMove: (AppItem) -> Unit,
    onToggleHide: (AppItem) -> Unit,
    onUninstall: ((AppItem) -> Unit)? = null
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }
    val installDateStr = remember(app.firstInstallTime) {
        if (app.firstInstallTime > 0) dateFormat.format(Date(app.firstInstallTime)) else context.getString(R.string.app_install_preinstalled)
    }
    val lastLaunchedStr = remember(app.lastLaunchedTime) {
        if (app.lastLaunchedTime > 0) dateFormat.format(Date(app.lastLaunchedTime)) else context.getString(R.string.app_last_launch_never)
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF0B1120),
            modifier = Modifier
                .width(480.dp)
                .border(1.dp, TvFocusGlow.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                .testTag("app_context_overlay")
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Bar with App Icon & Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val appBitmap = remember(app.iconDrawable) {
                        try {
                            app.iconDrawable?.toBitmap(96, 96)?.asImageBitmap()
                        } catch (e: Exception) {
                            null
                        }
                    }

                    if (appBitmap != null) {
                        Image(
                            bitmap = appBitmap,
                            contentDescription = app.label,
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(TvSurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = TvFocusGlow,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = app.label,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = app.packageName,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.55f),
                            fontSize = 11.sp
                        )
                        Row(
                            modifier = Modifier.padding(top = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // App Type Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(TvPrimary.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = app.appType.displayName,
                                    color = TvPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            // Category Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(TvFocusGlow.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = app.category,
                                    color = TvFocusGlow,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            if (app.fastLaunchKey != null) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFF59E0B).copy(alpha = 0.25f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Hotkey: ${app.fastLaunchKey}",
                                        color = Color(0xFFFBBF24),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Detailed App Information Grid Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(TvSurfaceVariant.copy(alpha = 0.45f))
                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.app_info_details),
                        color = TvFocusGlow,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        InfoColumn(label = stringResource(R.string.version_code), value = "v${app.versionName} (${app.versionCode})", modifier = Modifier.weight(1f))
                        InfoColumn(label = stringResource(R.string.launch_count), value = "${app.launchCount} launches", modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        InfoColumn(label = stringResource(R.string.install_date), value = installDateStr, modifier = Modifier.weight(1f))
                        InfoColumn(label = stringResource(R.string.last_used), value = lastLaunchedStr, modifier = Modifier.weight(1f))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.actions_header),
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Actions List
                OptionItem(
                    icon = Icons.Default.PlayArrow,
                    title = stringResource(R.string.launch_app),
                    subtitle = stringResource(R.string.open_app_desc, app.label),
                    onClick = {
                        onDismiss()
                        onLaunch(app)
                    }
                )

                OptionItem(
                    icon = Icons.Default.Settings,
                    title = stringResource(R.string.system_app_settings),
                    subtitle = stringResource(R.string.deep_stop_desc),
                    onClick = {
                        onDismiss()
                        onDeepStopInfo(app)
                    }
                )

                if (onUninstall != null) {
                    OptionItem(
                        icon = Icons.Default.Delete,
                        title = stringResource(R.string.uninstall_app),
                        subtitle = if (app.isSystem) stringResource(R.string.uninstall_system_app_warning) else stringResource(R.string.uninstall_desc),
                        isDestructive = true,
                        onClick = {
                            onDismiss()
                            onUninstall(app)
                        }
                    )
                }

                OptionItem(
                    icon = if (app.category == "Favorites") Icons.Default.Star else Icons.Default.StarOutline,
                    title = if (app.category == "Favorites") stringResource(R.string.remove_from_favorites) else stringResource(R.string.add_to_favorites),
                    subtitle = if (app.category == "Favorites") stringResource(R.string.favorited) else stringResource(R.string.standard_priority),
                    onClick = {
                        onDismiss()
                        onToggleFavorite(app)
                    }
                )

                OptionItem(
                    icon = Icons.Default.Category,
                    title = stringResource(R.string.change_category),
                    subtitle = stringResource(R.string.current_folder_label, app.category),
                    onClick = {
                        onDismiss()
                        onOpenCategoryMove(app)
                    }
                )

                OptionItem(
                    icon = Icons.Default.Key,
                    title = stringResource(R.string.map_hotkey),
                    subtitle = if (app.fastLaunchKey != null) stringResource(R.string.current_hotkey_label, app.fastLaunchKey) else stringResource(R.string.map_hotkey_desc),
                    onClick = {
                        onDismiss()
                        onOpenHotkeyAssign(app)
                    }
                )

                OptionItem(
                    icon = Icons.Default.Image,
                    title = stringResource(R.string.banner_fixer),
                    subtitle = stringResource(R.string.banner_fixer_desc),
                    onClick = {
                        onDismiss()
                        onOpenCustomBanner(app)
                    }
                )

                OptionItem(
                    icon = Icons.Default.VisibilityOff,
                    title = if (app.isHidden) stringResource(R.string.unhide_app) else stringResource(R.string.hide_app),
                    subtitle = if (app.isHidden) stringResource(R.string.visible_on_grid) else stringResource(R.string.stealth_vault_label),
                    onClick = {
                        onDismiss()
                        onToggleHide(app)
                    }
                )
            }
        }
    }
}

@Composable
private fun InfoColumn(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun OptionItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                when {
                    isFocused -> TvFocusGlow
                    isDestructive -> Color(0xFFEF4444).copy(alpha = 0.1f)
                    else -> TvSurfaceVariant.copy(alpha = 0.4f)
                }
            )
            .border(
                1.dp,
                if (isFocused) TvFocusGlow else (if (isDestructive) Color(0xFFEF4444).copy(alpha = 0.2f) else Color.Transparent),
                RoundedCornerShape(12.dp)
            )
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = when {
                isFocused -> Color.Black
                isDestructive -> Color(0xFFF87171)
                else -> TvFocusGlow
            },
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                color = when {
                    isFocused -> Color.Black
                    isDestructive -> Color(0xFFF87171)
                    else -> Color.White
                },
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                color = when {
                    isFocused -> Color.Black.copy(alpha = 0.8f)
                    isDestructive -> Color(0xFFF87171).copy(alpha = 0.8f)
                    else -> Color.White.copy(alpha = 0.6f)
                },
                fontSize = 11.sp
            )
        }
    }
}
