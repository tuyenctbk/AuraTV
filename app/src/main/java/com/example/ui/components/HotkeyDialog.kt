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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.AppItem
import com.example.ui.theme.TvFocusGlow
import com.example.ui.theme.TvSurfaceVariant

@Composable
fun HotkeyDialog(
    app: AppItem,
    onDismiss: () -> Unit,
    onSetHotkey: (Int?) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF0F172A),
            modifier = Modifier
                .width(400.dp)
                .border(1.dp, TvFocusGlow, RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = stringResource(R.string.hotkey_dialog_title, app.label),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = stringResource(R.string.hotkey_dialog_desc),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Number Grid 0 to 9
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    (0..4).forEach { num ->
                        HotkeyNumberButton(
                            num = num,
                            isSelected = app.fastLaunchKey == num,
                            onSelect = {
                                onSetHotkey(num)
                                onDismiss()
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    (5..9).forEach { num ->
                        HotkeyNumberButton(
                            num = num,
                            isSelected = app.fastLaunchKey == num,
                            onSelect = {
                                onSetHotkey(num)
                                onDismiss()
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    var isClearFocused by remember { mutableStateOf(false) }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isClearFocused) TvSurfaceVariant else Color.Transparent)
                            .onFocusChanged { isClearFocused = it.isFocused }
                            .focusable()
                            .clickable {
                                onSetHotkey(null)
                                onDismiss()
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(stringResource(R.string.clear_hotkey), color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }

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
}

@Composable
private fun HotkeyNumberButton(
    num: Int,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                when {
                    isFocused -> TvFocusGlow
                    isSelected -> Color(0xFF38BDF8).copy(alpha = 0.3f)
                    else -> TvSurfaceVariant
                }
            )
            .border(
                width = if (isSelected || isFocused) 2.dp else 1.dp,
                color = if (isFocused) Color.White else (if (isSelected) TvFocusGlow else Color.Transparent),
                shape = RoundedCornerShape(10.dp)
            )
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable { onSelect() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$num",
            color = if (isFocused) Color.Black else Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}
