package com.example.ui.components

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.StorageInfo
import com.example.ui.theme.TvFocusGlow
import com.example.ui.theme.TvPrimary
import com.example.ui.theme.TvSurfaceVariant
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HeaderBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    storageInfo: StorageInfo,
    showStorageWidget: Boolean,
    showClockWidget: Boolean,
    isVaultUnlocked: Boolean,
    onOpenVaultModal: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenHelp: (() -> Unit)? = null,
    onTriggerVoiceSearch: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var currentTimeStr by remember { mutableStateOf("") }
    var currentDateStr by remember { mutableStateOf("") }

    val context = LocalContext.current
    var batteryLevel by remember { mutableIntStateOf(-1) }
    var isBatteryLow by remember { mutableStateOf(false) }

    DisposableEffect(context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
                    val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                    val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                    if (level >= 0 && scale > 0) {
                        val pct = (level * 100) / scale
                        batteryLevel = pct
                        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
                        isBatteryLow = pct in 1..20 && !isCharging
                    }
                }
            }
        }
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val stickyIntent = context.registerReceiver(receiver, filter)
        if (stickyIntent != null) {
            val level = stickyIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = stickyIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val status = stickyIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            if (level >= 0 && scale > 0) {
                val pct = (level * 100) / scale
                batteryLevel = pct
                val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
                isBatteryLow = pct in 1..20 && !isCharging
            }
        }
        onDispose {
            try { context.unregisterReceiver(receiver) } catch (e: Exception) {}
        }
    }

    LaunchedEffect(Unit) {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dateFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
        while (true) {
            val now = Date()
            currentTimeStr = timeFormat.format(now)
            currentDateStr = dateFormat.format(now)
            delay(1000)
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Branding & Storage Widget
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "AURA",
                style = MaterialTheme.typography.titleLarge,
                color = TvPrimary,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )
            Text(
                text = "TV",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            if (showStorageWidget && storageInfo.totalBytes > 0) {
                Spacer(modifier = Modifier.width(16.dp))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(TvSurfaceVariant.copy(alpha = 0.6f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Storage,
                        contentDescription = "Storage",
                        tint = TvFocusGlow,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = storageInfo.formatFreeGb(),
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Center: Real-time Search Field with smooth animations
        var isSearchFocused by remember { mutableStateOf(false) }
        val animatedWidth by animateDpAsState(
            targetValue = if (isSearchFocused) 360.dp else 300.dp,
            label = "searchWidth"
        )
        val animatedBgColor by animateColorAsState(
            targetValue = if (isSearchFocused) TvSurfaceVariant else Color.White.copy(alpha = 0.08f),
            label = "searchBg"
        )
        val animatedBorderColor by animateColorAsState(
            targetValue = if (isSearchFocused) TvFocusGlow else Color.White.copy(alpha = 0.15f),
            label = "searchBorder"
        )
        val animatedBorderWidth by animateDpAsState(
            targetValue = if (isSearchFocused) 2.dp else 1.dp,
            label = "searchBorderWidth"
        )

        Row(
            modifier = Modifier
                .width(animatedWidth)
                .height(42.dp)
                .clip(RoundedCornerShape(21.dp))
                .background(animatedBgColor)
                .border(
                    width = animatedBorderWidth,
                    color = animatedBorderColor,
                    shape = RoundedCornerShape(21.dp)
                )
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = if (isSearchFocused) TvFocusGlow else Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))

            BasicTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                singleLine = true,
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                ),
                cursorBrush = SolidColor(TvFocusGlow),
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged { isSearchFocused = it.isFocused }
                    .focusable()
                    .testTag("search_input"),
                decorationBox = { innerTextField ->
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = stringResource(R.string.search_hint),
                            color = Color.White.copy(alpha = 0.4f),
                            fontSize = 13.sp
                        )
                    }
                    innerTextField()
                }
            )

            if (searchQuery.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear",
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier
                        .size(18.dp)
                        .clickable { onSearchQueryChange("") }
                )
            } else if (onTriggerVoiceSearch != null) {
                var isMicFocused by remember { mutableStateOf(false) }
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice Search",
                    tint = if (isMicFocused) TvFocusGlow else TvPrimary,
                    modifier = Modifier
                        .size(20.dp)
                        .onFocusChanged { isMicFocused = it.isFocused }
                        .focusable()
                        .clickable { onTriggerVoiceSearch() }
                )
            }
        }

        // Right: Clock, Low Battery Indicator & Quick Action Buttons
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            // Low Battery Indicator Badge (Appears only when battery is low)
            if (isBatteryLow && batteryLevel > 0) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFEF4444).copy(alpha = 0.25f))
                        .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.BatteryAlert,
                        contentDescription = "Low Battery",
                        tint = Color(0xFFF87171),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$batteryLevel%",
                        color = Color(0xFFF87171),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
            }
            if (showClockWidget && currentTimeStr.isNotEmpty()) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = currentTimeStr,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = currentDateStr,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
            }

            // Vault Lock Status Button
            var isVaultBtnFocused by remember { mutableStateOf(false) }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isVaultBtnFocused) TvFocusGlow else Color.White.copy(alpha = 0.1f))
                    .onFocusChanged { isVaultBtnFocused = it.isFocused }
                    .focusable()
                    .clickable { onOpenVaultModal() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isVaultUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                    contentDescription = "Vault",
                    tint = if (isVaultBtnFocused) Color.Black else (if (isVaultUnlocked) Color(0xFF10B981) else Color.White),
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Help Legend Button
            if (onOpenHelp != null) {
                var isHelpFocused by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isHelpFocused) TvFocusGlow else Color.White.copy(alpha = 0.1f))
                        .onFocusChanged { isHelpFocused = it.isFocused }
                        .focusable()
                        .clickable { onOpenHelp() }
                        .testTag("btn_help_legend"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.HelpOutline,
                        contentDescription = "Help Guide",
                        tint = if (isHelpFocused) Color.Black else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))
            }

            // Settings Button
            var isSettingsFocused by remember { mutableStateOf(false) }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isSettingsFocused) TvFocusGlow else Color.White.copy(alpha = 0.1f))
                    .onFocusChanged { isSettingsFocused = it.isFocused }
                    .focusable()
                    .clickable { onOpenSettings() }
                    .testTag("btn_settings"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = if (isSettingsFocused) Color.Black else Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
