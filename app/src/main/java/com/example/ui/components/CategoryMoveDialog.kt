package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.AppItem
import com.example.ui.theme.TvFocusGlow
import com.example.ui.theme.TvPrimary
import com.example.ui.theme.TvSurfaceVariant

@Composable
fun CategoryMoveDialog(
    app: AppItem,
    existingCategories: List<String>,
    onDismiss: () -> Unit,
    onMoveCategory: (String) -> Unit
) {
    var newCategoryText by remember { mutableStateOf("") }
    val categories = remember(existingCategories) {
        listOf("Favorites", "TV Apps", "Sideloaded", "Streaming", "Games", "Work", "System")
            .plus(existingCategories)
            .distinct()
    }

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
                    text = stringResource(R.string.category_dialog_title, app.label),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = stringResource(R.string.category_dialog_desc),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Custom new category field
                var isInputFocused by remember { mutableStateOf(false) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isInputFocused) TvSurfaceVariant else Color.White.copy(alpha = 0.1f))
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        value = newCategoryText,
                        onValueChange = { newCategoryText = it },
                        singleLine = true,
                        textStyle = TextStyle(color = Color.White, fontSize = 13.sp),
                        cursorBrush = SolidColor(TvFocusGlow),
                        modifier = Modifier
                            .weight(1f)
                            .onFocusChanged { isInputFocused = it.isFocused }
                            .focusable(),
                        decorationBox = { innerTextField ->
                            if (newCategoryText.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.create_category_hint),
                                    color = Color.White.copy(alpha = 0.4f),
                                    fontSize = 12.sp
                                )
                            }
                            innerTextField()
                        }
                    )

                    if (newCategoryText.trim().isNotEmpty()) {
                        var isAddFocused by remember { mutableStateOf(false) }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isAddFocused) TvFocusGlow else TvPrimary)
                                .onFocusChanged { isAddFocused = it.isFocused }
                                .focusable()
                                .clickable {
                                    onMoveCategory(newCategoryText.trim())
                                    onDismiss()
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add",
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Category chips
                categories.forEach { catName ->
                    CategoryOptionItem(
                        catName = catName,
                        isSelected = app.category == catName,
                        onSelect = {
                            onMoveCategory(catName)
                            onDismiss()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

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
                    Text(stringResource(R.string.cancel), color = Color.White, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun CategoryOptionItem(
    catName: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isFocused) TvFocusGlow else (if (isSelected) TvPrimary.copy(alpha = 0.2f) else TvSurfaceVariant.copy(alpha = 0.4f)))
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable { onSelect() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Folder,
            contentDescription = catName,
            tint = if (isFocused) Color.Black else (if (isSelected) TvPrimary else Color.White.copy(alpha = 0.7f)),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = catName,
            color = if (isFocused) Color.Black else Color.White,
            fontWeight = if (isSelected || isFocused) FontWeight.Bold else FontWeight.Normal,
            fontSize = 13.sp
        )
    }
}
