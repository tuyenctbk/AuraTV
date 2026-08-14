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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TouchApp
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.TvFocusGlow
import com.example.ui.theme.TvPrimary
import com.example.ui.theme.TvSurfaceVariant

@Composable
fun HelpOverlayDialog(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF0F172A),
            modifier = Modifier
                .width(540.dp)
                .padding(16.dp)
                .border(1.dp, TvFocusGlow.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .testTag("help_overlay_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = "Help Legend",
                            tint = TvPrimary,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "TV Remote Control Guide",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Shortcuts & Navigation Controls",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }

                    var isCloseFocused by remember { mutableStateOf(false) }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
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

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                Spacer(modifier = Modifier.height(16.dp))

                // Shortcuts Legend Items
                HelpShortcutRow(
                    icon = Icons.Default.Navigation,
                    title = "D-Pad Arrows (Up / Down / Left / Right)",
                    description = "Navigate through app icons grid, top header bar, search bar, and custom category folders."
                )

                HelpShortcutRow(
                    icon = Icons.Default.TouchApp,
                    title = "Long Press OK / Select",
                    description = "Opens the App Context Menu to trigger Deep Stop (Force Stop), set custom hotkeys, or edit custom banners."
                )

                HelpShortcutRow(
                    icon = Icons.Default.Settings,
                    title = "Menu Key / Settings Icon",
                    description = "Opens Launcher Settings (4K Wallpaper engine, custom accent colors, performance cache cleaner, backup & restore)."
                )

                HelpShortcutRow(
                    icon = Icons.Default.Mic,
                    title = "Mic / Assistant Button",
                    description = "Triggers voice-to-text search listener on supported Android TV remotes."
                )

                HelpShortcutRow(
                    icon = Icons.Default.Numbers,
                    title = "Number Keys (0 - 9)",
                    description = "Instantly launches mapped hotkey applications from anywhere on the launcher."
                )

                HelpShortcutRow(
                    icon = Icons.Default.Security,
                    title = "Stealth Vault Code",
                    description = "Press D-Pad combo: UP, UP, DOWN, LEFT to unlock or lock hidden vault applications."
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Action Got It Button
                var isGotItFocused by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isGotItFocused) TvFocusGlow else TvPrimary)
                        .onFocusChanged { isGotItFocused = it.isFocused }
                        .focusable()
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Got It, Close Legend",
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun HelpShortcutRow(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(TvPrimary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TvFocusGlow,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }
    }
}
