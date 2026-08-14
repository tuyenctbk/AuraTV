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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AppItem
import com.example.ui.theme.TvFocusGlow
import com.example.ui.theme.TvPrimary
import com.example.ui.theme.TvSurfaceVariant

@Composable
fun CustomBannerDialog(
    app: AppItem,
    onDismiss: () -> Unit,
    onSaveBannerPath: (String?) -> Unit
) {
    var pathText by remember { mutableStateOf(app.customBannerPath ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF0F172A),
            modifier = Modifier
                .width(420.dp)
                .border(1.dp, TvFocusGlow, RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Banner Fixer: ${app.label}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Enter local file path or image URL for custom 16:9 banner.\n" +
                            "Tip: Save images to /sdcard/GhostLauncher/Banners/${app.packageName}.png for auto-detection.",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                var isInputFocused by remember { mutableStateOf(false) }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isInputFocused) TvSurfaceVariant else Color.White.copy(alpha = 0.1f))
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    BasicTextField(
                        value = pathText,
                        onValueChange = { pathText = it },
                        singleLine = true,
                        textStyle = TextStyle(
                            color = Color.White,
                            fontSize = 13.sp
                        ),
                        cursorBrush = SolidColor(TvFocusGlow),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { isInputFocused = it.isFocused }
                            .focusable(),
                        decorationBox = { innerTextField ->
                            if (pathText.isEmpty()) {
                                Text(
                                    text = "/sdcard/GhostLauncher/Banners/banner.png",
                                    color = Color.White.copy(alpha = 0.4f),
                                    fontSize = 12.sp
                                )
                            }
                            innerTextField()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var isClearFocused by remember { mutableStateOf(false) }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isClearFocused) TvSurfaceVariant else Color.Transparent)
                            .onFocusChanged { isClearFocused = it.isFocused }
                            .focusable()
                            .clickable {
                                onSaveBannerPath(null)
                                onDismiss()
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text("Reset Banner", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }

                    Row {
                        var isCancelFocused by remember { mutableStateOf(false) }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isCancelFocused) TvSurfaceVariant else Color.Transparent)
                                .onFocusChanged { isCancelFocused = it.isFocused }
                                .focusable()
                                .clickable { onDismiss() }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text("Cancel", color = Color.White, fontSize = 13.sp)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        var isSaveFocused by remember { mutableStateOf(false) }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSaveFocused) TvFocusGlow else TvPrimary)
                                .onFocusChanged { isSaveFocused = it.isFocused }
                                .focusable()
                                .clickable {
                                    onSaveBannerPath(pathText.trim().takeIf { it.isNotEmpty() })
                                    onDismiss()
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text("Save", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}
