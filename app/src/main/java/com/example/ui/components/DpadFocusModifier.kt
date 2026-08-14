package com.example.ui.components

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import com.example.ui.theme.TvFocusGlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun Modifier.dpadFocusEffect(
    isFocused: Boolean,
    scaleAmount: Float = 1.1f,
    reduceMotion: Boolean = false,
    shape: Shape = RoundedCornerShape(12.dp),
    glowColor: Color = TvFocusGlow,
    usePaletteExtraction: Boolean = true,
    borderWidth: Dp = 3.dp,
    elevation: Dp = 16.dp,
    sourceDrawable: Drawable? = null
): Modifier = composed {
    var extractedColor by remember(sourceDrawable, usePaletteExtraction) { mutableStateOf<Color?>(null) }

    LaunchedEffect(sourceDrawable, usePaletteExtraction) {
        if (usePaletteExtraction && sourceDrawable != null) {
            withContext(Dispatchers.Default) {
                try {
                    val bitmap = if (sourceDrawable is BitmapDrawable && sourceDrawable.bitmap != null) {
                        sourceDrawable.bitmap
                    } else {
                        val w = sourceDrawable.intrinsicWidth.coerceAtLeast(1)
                        val h = sourceDrawable.intrinsicHeight.coerceAtLeast(1)
                        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
                        val canvas = android.graphics.Canvas(bmp)
                        sourceDrawable.setBounds(0, 0, w, h)
                        sourceDrawable.draw(canvas)
                        bmp
                    }
                    val palette = Palette.from(bitmap).generate()
                    val swatch = palette.vibrantSwatch ?: palette.lightVibrantSwatch ?: palette.dominantSwatch
                    if (swatch != null) {
                        extractedColor = Color(swatch.rgb)
                    }
                } catch (e: Exception) {
                    // Fallback to default
                }
            }
        }
    }

    val activeGlowColor = if (usePaletteExtraction) (extractedColor ?: glowColor) else glowColor

    // Subtle Infinite Pulse Animation for focused selection
    val infiniteTransition = rememberInfiniteTransition(label = "dpadPulseTransition")
    val pulseScaleMultiplier by if (isFocused && !reduceMotion) {
        infiniteTransition.animateFloat(
            initialValue = 1.0f,
            targetValue = 1.025f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1100, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulseScale"
        )
    } else {
        rememberUpdatedState(1.0f)
    }

    val targetScale = if (isFocused) {
        (if (reduceMotion) 1.03f else scaleAmount) * pulseScaleMultiplier
    } else 1.0f

    val scale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = spring(stiffness = 400f),
        label = "dpadFocusScale"
    )

    val animatedGlowColor by animateColorAsState(
        targetValue = if (isFocused) activeGlowColor else Color.White.copy(alpha = 0.1f),
        label = "dpadGlowColor"
    )

    this
        .scale(scale)
        .shadow(
            elevation = if (isFocused) elevation else 2.dp,
            shape = shape,
            ambientColor = if (isFocused) activeGlowColor else Color.Black,
            spotColor = if (isFocused) activeGlowColor else Color.Black
        )
        .border(
            width = if (isFocused) borderWidth else 1.dp,
            color = animatedGlowColor,
            shape = shape
        )
}
