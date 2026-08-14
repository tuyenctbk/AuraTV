package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.ui.theme.TvBackground

@Composable
fun WallpaperBackground(
    wallpaperUri: String?,
    dimLevel: Float,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize().background(TvBackground)) {
        if (!wallpaperUri.isNullOrEmpty()) {
            if (wallpaperUri.startsWith("preset_")) {
                // Procedural dark TV background presets
                PresetBackgroundCanvas(preset = wallpaperUri)
            } else {
                AsyncImage(
                    model = wallpaperUri,
                    contentDescription = "Wallpaper",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        } else {
            PresetBackgroundCanvas(preset = "preset_dark_cyber")
        }

        // Dimmer overlay
        if (dimLevel > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = dimLevel.coerceIn(0f, 0.95f)))
            )
        }
    }
}

@Composable
private fun PresetBackgroundCanvas(preset: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "wallpaperAurora")
    val shiftX by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shiftX"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        when (preset) {
            "preset_neon_aurora" -> {
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF312E81),
                            Color(0xFF1E1B4B),
                            Color(0xFF0F172A),
                            Color(0xFF030712)
                        ),
                        center = Offset(width * shiftX, height * 0.35f),
                        radius = width * 0.85f
                    )
                )
            }
            "preset_cyber_grid" -> {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0B132B),
                            Color(0xFF1C2541),
                            Color(0xFF0F172A),
                            Color(0xFF020617)
                        )
                    )
                )
            }
            else -> {
                // Default Dark Cyber Slate with subtle ambient glow
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF1E293B),
                            Color(0xFF0F172A),
                            Color(0xFF020617)
                        ),
                        center = Offset(width * (1f - shiftX * 0.5f), height * 0.25f),
                        radius = width * 0.9f
                    )
                )
            }
        }
    }
}
