package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CyberColorScheme = darkColorScheme(
    primary = CyberCyan,
    onPrimary = Color.Black,
    secondary = NeonPink,
    onSecondary = Color.White,
    tertiary = NeonGreen,
    onTertiary = Color.Black,
    background = CyberBlack,
    onBackground = Color.White,
    surface = CyberDarkSurface,
    onSurface = Color.White,
    surfaceVariant = CyberMutedSurface,
    onSurfaceVariant = MutedText,
    outline = GlassBorder
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CyberColorScheme,
        typography = Typography,
        content = content
    )
}
