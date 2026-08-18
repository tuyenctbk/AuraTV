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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
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
fun FolderManagerDialog(
    allApps: List<AppItem>,
    existingCategories: List<String>,
    onMoveAppToCategory: (AppItem, String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedFolder by remember { mutableStateOf<String?>(null) }
    var newFolderName by remember { mutableStateOf("") }
    var showCreateInput by remember { mutableStateOf(false) }

    val folders = remember(existingCategories, allApps) {
        val list = existingCategories.filter { it != "All" && it != "Favorites" }.toMutableList()
        val customCats = allApps.map { it.category }.distinct().filter { it != "All" && it != "Favorites" && !list.contains(it) }
        list.addAll(customCats)
        list
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF0B1120),
            modifier = Modifier
                .width(600.dp)
                .fillMaxHeight(0.85f)
                .border(1.dp, TvFocusGlow.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                .testTag("folder_manager_dialog")
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.folder_manager),
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (selectedFolder == null) stringResource(R.string.folder_manager_desc) else stringResource(R.string.folder_title_label, selectedFolder ?: ""),
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
                            .clickable {
                                if (selectedFolder != null) {
                                    selectedFolder = null
                                } else {
                                    onDismiss()
                                }
                            },
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

                if (selectedFolder == null) {
                    // Folders Overview & Create New Folder
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.user_defined_folders),
                            color = Color.White.copy(alpha = 0.4f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        var isCreateFocused by remember { mutableStateOf(false) }
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isCreateFocused) TvFocusGlow else TvPrimary.copy(alpha = 0.2f))
                                .border(1.dp, if (isCreateFocused) TvFocusGlow else TvPrimary.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .onFocusChanged { isCreateFocused = it.isFocused }
                                .focusable()
                                .clickable { showCreateInput = !showCreateInput }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(R.string.new_folder),
                                tint = if (isCreateFocused) Color.Black else TvPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(R.string.new_folder),
                                color = if (isCreateFocused) Color.Black else Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (showCreateInput) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(TvSurfaceVariant.copy(alpha = 0.6f))
                                .border(1.dp, TvFocusGlow, RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BasicTextField(
                                value = newFolderName,
                                onValueChange = { newFolderName = it },
                                singleLine = true,
                                textStyle = TextStyle(color = Color.White, fontSize = 13.sp),
                                cursorBrush = SolidColor(TvFocusGlow),
                                modifier = Modifier.weight(1f),
                                decorationBox = { innerTextField ->
                                    if (newFolderName.isEmpty()) {
                                        Text(stringResource(R.string.enter_folder_name), color = Color.White.copy(alpha = 0.35f), fontSize = 13.sp)
                                    }
                                    innerTextField()
                                }
                            )

                            var isConfirmFocused by remember { mutableStateOf(false) }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isConfirmFocused) TvFocusGlow else TvPrimary)
                                    .onFocusChanged { isConfirmFocused = it.isFocused }
                                    .focusable()
                                    .clickable {
                                        val trimmed = newFolderName.trim()
                                        if (trimmed.isNotEmpty()) {
                                            selectedFolder = trimmed
                                            newFolderName = ""
                                            showCreateInput = false
                                        }
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.create),
                                    color = if (isConfirmFocused) Color.Black else Color.Black,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(folders) { folderName ->
                            val appCount = allApps.count { it.category == folderName }
                            var isFolderFocused by remember { mutableStateOf(false) }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isFolderFocused) TvFocusGlow else TvSurfaceVariant.copy(alpha = 0.4f))
                                    .border(1.dp, if (isFolderFocused) TvFocusGlow else Color.Transparent, RoundedCornerShape(14.dp))
                                    .onFocusChanged { isFolderFocused = it.isFocused }
                                    .focusable()
                                    .clickable { selectedFolder = folderName }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FolderOpen,
                                    contentDescription = folderName,
                                    tint = if (isFolderFocused) Color.Black else TvFocusGlow,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = folderName,
                                        color = if (isFolderFocused) Color.Black else Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = stringResource(R.string.apps_count_inside, appCount),
                                        color = if (isFolderFocused) Color.Black.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.55f),
                                        fontSize = 11.sp
                                    )
                                }
                                Text(
                                    text = stringResource(R.string.manage_apps_label),
                                    color = if (isFolderFocused) Color.Black else TvPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                } else {
                    // Assign/unassign apps to selectedFolder
                    val currentTargetFolder = selectedFolder ?: ""
                    Text(
                        text = stringResource(R.string.toggle_apps_folder_desc, currentTargetFolder),
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(allApps, key = { it.packageName }) { app ->
                            val isInFolder = app.category == currentTargetFolder
                            var isAppFocused by remember { mutableStateOf(false) }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isAppFocused) TvFocusGlow else TvSurfaceVariant.copy(alpha = 0.35f))
                                    .border(1.dp, if (isAppFocused) TvFocusGlow else Color.Transparent, RoundedCornerShape(12.dp))
                                    .onFocusChanged { isAppFocused = it.isFocused }
                                    .focusable()
                                    .clickable {
                                        val newCat = if (isInFolder) {
                                            when (app.appType) {
                                                com.example.data.model.AppType.LEANBACK -> "TV Apps"
                                                com.example.data.model.AppType.SIDELOADED -> "Sideloaded"
                                                com.example.data.model.AppType.SYSTEM -> "System"
                                            }
                                        } else {
                                            currentTargetFolder
                                        }
                                        onMoveAppToCategory(app, newCat)
                                    }
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
                                        color = if (isAppFocused) Color.Black else Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = stringResource(R.string.current_folder_label, app.category),
                                        color = if (isAppFocused) Color.Black.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.55f),
                                        fontSize = 11.sp
                                    )
                                }

                                if (isInFolder) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "In Folder",
                                        tint = if (isAppFocused) Color.Black else Color(0xFF10B981),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
