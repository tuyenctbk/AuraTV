package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AppItem
import com.example.ui.theme.TvFocusGlow
import com.example.ui.theme.TvSurfaceVariant

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
    onToggleHide: (AppItem) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF0F172A),
            modifier = Modifier
                .width(380.dp)
                .border(1.dp, TvFocusGlow.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Header Title
                Text(
                    text = app.label,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${app.packageName} • v${app.versionName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Menu Actions
                OptionItem(
                    icon = Icons.Default.PlayArrow,
                    title = "Launch Application",
                    subtitle = "Open ${app.label}",
                    onClick = {
                        onDismiss()
                        onLaunch(app)
                    }
                )

                OptionItem(
                    icon = if (app.category == "Favorites") Icons.Default.Star else Icons.Default.StarOutline,
                    title = if (app.category == "Favorites") "Remove from Favorites" else "Add to Favorites",
                    subtitle = "Toggle row priority",
                    onClick = {
                        onDismiss()
                        onToggleFavorite(app)
                    }
                )

                OptionItem(
                    icon = Icons.Default.Info,
                    title = "Deep Stop (Force Stop Background App)",
                    subtitle = "Open system App Info to force-stop background memory consumption",
                    onClick = {
                        onDismiss()
                        onDeepStopInfo(app)
                    }
                )

                OptionItem(
                    icon = Icons.Default.Image,
                    title = "Banner Fixer",
                    subtitle = if (!app.customBannerPath.isNullOrEmpty()) "Custom banner active" else "Assign high-res custom banner path",
                    onClick = {
                        onDismiss()
                        onOpenCustomBanner(app)
                    }
                )

                OptionItem(
                    icon = Icons.Default.Key,
                    title = "Map Remote Hotkey (0-9)",
                    subtitle = if (app.fastLaunchKey != null) "Current Hotkey: [${app.fastLaunchKey}]" else "Assign fast launcher number key",
                    onClick = {
                        onDismiss()
                        onOpenHotkeyAssign(app)
                    }
                )

                OptionItem(
                    icon = Icons.Default.Category,
                    title = "Change Category / Dynamic Folder",
                    subtitle = "Current: ${app.category}",
                    onClick = {
                        onDismiss()
                        onOpenCategoryMove(app)
                    }
                )

                OptionItem(
                    icon = Icons.Default.VisibilityOff,
                    title = if (app.isHidden) "Unhide App" else "Hide App (Stealth Vault)",
                    subtitle = if (app.isHidden) "App is currently hidden" else "Move to Stealth Vault",
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
private fun OptionItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isFocused) TvFocusGlow else TvSurfaceVariant.copy(alpha = 0.5f))
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isFocused) Color.Black else TvFocusGlow,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                color = if (isFocused) Color.Black else Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                color = if (isFocused) Color.Black.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.6f),
                fontSize = 11.sp
            )
        }
    }
}
