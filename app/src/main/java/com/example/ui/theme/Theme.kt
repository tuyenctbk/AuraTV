package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val GhostDarkColorScheme = darkColorScheme(
    primary = TvPrimary,
    secondary = TvSecondary,
    tertiary = TvTertiary,
    background = TvBackground,
    surface = TvSurface,
    surfaceVariant = TvSurfaceVariant,
    onBackground = TvOnBackground,
    onSurface = TvOnSurface
)

@Composable
fun GhostLauncherTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GhostDarkColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    GhostLauncherTheme(content = content)
}

