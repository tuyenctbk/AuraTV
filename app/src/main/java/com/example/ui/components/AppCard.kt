package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.data.model.AppItem
import com.example.data.model.AppType
import com.example.ui.theme.TvFocusGlow
import com.example.ui.theme.TvSideloadBadge
import com.example.ui.theme.TvSurfaceVariant

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppCard(
    app: AppItem,
    cardRatio: String, // "16:9" or "1:1"
    showLabels: Boolean,
    reduceMotion: Boolean,
    onLaunch: (AppItem) -> Unit,
    onOptions: (AppItem) -> Unit,
    modifier: Modifier = Modifier,
    accentColorHex: String = "#D0BCFF",
    usePaletteExtraction: Boolean = true
) {
    var isFocused by remember { mutableStateOf(false) }
    val aspectRatioValue = if (cardRatio == "16:9") 16f / 9f else 1f
    val shape = RoundedCornerShape(12.dp)
    val context = LocalContext.current

    val parsedGlowColor = remember(accentColorHex) {
        try {
            Color(android.graphics.Color.parseColor(accentColorHex))
        } catch (e: Exception) {
            TvFocusGlow
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .zIndex(if (isFocused) 10f else 1f)
            .padding(6.dp)
            .testTag("app_card_${app.packageName}")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(aspectRatioValue)
                .clip(shape)
                .dpadFocusEffect(
                    isFocused = isFocused,
                    scaleAmount = 1.1f,
                    reduceMotion = reduceMotion,
                    shape = shape,
                    glowColor = parsedGlowColor,
                    usePaletteExtraction = usePaletteExtraction,
                    sourceDrawable = app.iconDrawable ?: app.bannerDrawable
                )
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF1C1B1F),
                            Color(0xFF0A0A0C)
                        )
                    )
                )
                .onFocusChanged { isFocused = it.isFocused }
                .focusable()
                .combinedClickable(
                    onClick = { onLaunch(app) },
                    onLongClick = { onOptions(app) }
                )
                .onKeyEvent { keyEvent ->
                    if (keyEvent.key == Key.Menu || keyEvent.key == Key.Guide) {
                        onOptions(app)
                        true
                    } else if (keyEvent.key == Key.DirectionCenter || keyEvent.key == Key.Enter || keyEvent.key == Key.NumPadEnter) {
                        onLaunch(app)
                        true
                    } else {
                        false
                    }
                }
        ) {
            val customBanner = app.customBannerPath
            val bannerDrawable = app.bannerDrawable

            when {
                // Custom banner set by user
                !customBanner.isNullOrEmpty() -> {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(customBanner)
                            .crossfade(true)
                            .build(),
                        contentDescription = app.label,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        error = { FallbackCardContent(app) }
                    )
                }
                // Native TV Banner loaded via Coil / Drawable
                bannerDrawable != null && cardRatio == "16:9" -> {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(bannerDrawable)
                            .crossfade(true)
                            .build(),
                        contentDescription = app.label,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        error = { FallbackCardContent(app) }
                    )
                }
                // Missing native banner -> Fallback with app icon centered on blurred gradient background
                else -> {
                    FallbackCardContent(app)
                }
            }

            // Top Left Badges
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(6.dp)
            ) {
                if (app.category == "Favorites") {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFF59E0B))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Favorite",
                            tint = Color.Black,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            // Top Right Hotkey / Sideload Badge
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
            ) {
                if (app.fastLaunchKey != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(TvFocusGlow)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "[${app.fastLaunchKey}]",
                            color = Color.Black,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else if (app.appType == AppType.SIDELOADED) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(TvSideloadBadge.copy(alpha = 0.9f))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "MOBILE",
                            color = Color.Black,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        if (showLabels) {
            Spacer(modifier = Modifier.height(4.dp))
            val animatedLabelColor by animateColorAsState(
                targetValue = if (isFocused) parsedGlowColor else Color.White.copy(alpha = 0.85f),
                label = "labelColor"
            )
            val animatedLabelScale by animateFloatAsState(
                targetValue = if (isFocused) 1.05f else 1.0f,
                label = "labelTextScale"
            )
            Text(
                text = app.label,
                style = MaterialTheme.typography.bodyMedium,
                color = animatedLabelColor,
                fontWeight = if (isFocused) FontWeight.Bold else FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.graphicsLayer {
                    scaleX = animatedLabelScale
                    scaleY = animatedLabelScale
                }
            )
        }
    }
}

@Composable
private fun FallbackCardContent(app: AppItem) {
    val iconDrawable = app.iconDrawable
    val bitmap = remember(iconDrawable) {
        if (iconDrawable != null) {
            try {
                val bmp = android.graphics.Bitmap.createBitmap(
                    iconDrawable.intrinsicWidth.coerceAtLeast(1),
                    iconDrawable.intrinsicHeight.coerceAtLeast(1),
                    android.graphics.Bitmap.Config.ARGB_8888
                )
                val canvas = android.graphics.Canvas(bmp)
                iconDrawable.setBounds(0, 0, canvas.width, canvas.height)
                iconDrawable.draw(canvas)
                bmp.asImageBitmap()
            } catch (e: Exception) {
                null
            }
        } else null
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        TvSurfaceVariant.copy(alpha = 0.9f),
                        Color(0xFF030712)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val gridColor = Color.White.copy(alpha = 0.035f)
            val spacing = 24.dp.toPx()
            
            var x = 0f
            while (x < w) {
                drawLine(gridColor, Offset(x, 0f), Offset(x, h), strokeWidth = 1f)
                x += spacing
            }
            var y = 0f
            while (y < h) {
                drawLine(gridColor, Offset(0f, y), Offset(w, y), strokeWidth = 1f)
                y += spacing
            }
            
            // Outer corner bracket decors
            val accentColor = Color.White.copy(alpha = 0.15f)
            val len = 8.dp.toPx()
            val offset = 4.dp.toPx()
            
            // Top-left
            drawLine(accentColor, Offset(offset, offset), Offset(offset + len, offset), strokeWidth = 1.5f)
            drawLine(accentColor, Offset(offset, offset), Offset(offset, offset + len), strokeWidth = 1.5f)
            // Bottom-right
            drawLine(accentColor, Offset(w - offset, h - offset), Offset(w - offset - len, h - offset), strokeWidth = 1.5f)
            drawLine(accentColor, Offset(w - offset, h - offset), Offset(w - offset, h - offset - len), strokeWidth = 1.5f)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = app.label,
                    modifier = Modifier.size(48.dp)
                )
            } else {
                Icon(
                    imageVector = if (app.appType == AppType.LEANBACK) Icons.Default.Tv else Icons.Default.Android,
                    contentDescription = app.label,
                    tint = TvFocusGlow,
                    modifier = Modifier.size(40.dp)
                )
            }
 
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = app.label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.9f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
