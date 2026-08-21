package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
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

@Composable
fun AppVisibilityDialog(
    allApps: List<AppItem>,
    onToggleVisibility: (AppItem) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredApps = remember(allApps, searchQuery) {
        if (searchQuery.isBlank()) allApps
        else allApps.filter { it.label.contains(searchQuery, ignoreCase = true) || it.packageName.contains(searchQuery, ignoreCase = true) }
    }

    val hiddenCount = remember(allApps) { allApps.count { it.isHidden } }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF0B1120),
            modifier = Modifier
                .width(520.dp)
                .fillMaxHeight(0.85f)
                .border(1.dp, TvFocusGlow.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                .testTag("app_visibility_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.app_visibility_manager),
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(R.string.total_hidden_summary, allApps.size, hiddenCount),
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp
                        )
                    }

                    var isCloseFocused by remember { mutableStateOf(false) }
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(if (isCloseFocused) TvFocusGlow else TvSurfaceVariant.copy(alpha = 0.6f))
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

                Spacer(modifier = Modifier.height(14.dp))

                // Search Filter
                var isSearchFocused by remember { mutableStateOf(false) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSearchFocused) TvSurfaceVariant else TvSurfaceVariant.copy(alpha = 0.4f))
                        .border(1.dp, if (isSearchFocused) TvFocusGlow else Color.White.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = if (isSearchFocused) TvFocusGlow else Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        singleLine = true,
                        textStyle = TextStyle(color = Color.White, fontSize = 13.sp),
                        cursorBrush = SolidColor(TvFocusGlow),
                        modifier = Modifier
                            .weight(1f)
                            .onFocusChanged { isSearchFocused = it.isFocused }
                            .focusable(),
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text(stringResource(R.string.filter_apps_hint), color = Color.White.copy(alpha = 0.35f), fontSize = 13.sp)
                            }
                            innerTextField()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Apps List
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredApps, key = { it.packageName }) { app ->
                        AppVisibilityItem(
                            app = app,
                            onToggle = { onToggleVisibility(app) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AppVisibilityItem(
    app: AppItem,
    onToggle: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isFocused) TvFocusGlow else TvSurfaceVariant.copy(alpha = 0.35f))
            .border(1.dp, if (isFocused) TvFocusGlow else Color.Transparent, RoundedCornerShape(12.dp))
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable { onToggle() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val appBitmap = remember(app.iconDrawable) {
            try {
                app.iconDrawable?.toBitmap(64, 64)?.asImageBitmap()
            } catch (e: Exception) {
                null
            }
        }

        if (appBitmap != null) {
            Image(
                bitmap = appBitmap,
                contentDescription = app.label,
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        } else {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(TvSurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Apps,
                    contentDescription = null,
                    tint = TvFocusGlow,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = app.label,
                color = if (isFocused) Color.Black else Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = if (app.isHidden) stringResource(R.string.hidden_stealth_vault) else stringResource(R.string.visible_on_grid),
                color = if (isFocused) Color.Black.copy(alpha = 0.7f) else (if (app.isHidden) Color(0xFFEF4444) else Color(0xFF10B981)),
                fontSize = 11.sp
            )
        }

        Icon(
            imageVector = if (app.isHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
            contentDescription = if (app.isHidden) stringResource(R.string.hidden_stealth_vault) else stringResource(R.string.visible_on_grid),
            tint = if (isFocused) Color.Black else (if (app.isHidden) Color(0xFFEF4444) else Color(0xFF10B981)),
            modifier = Modifier.size(22.dp)
        )
    }
}
