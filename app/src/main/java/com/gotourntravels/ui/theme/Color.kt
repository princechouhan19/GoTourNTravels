package com.gotourntravels.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Royal Rajasthan palette
val Maroon = Color(0xFF8B1E3F)
val MaroonDark = Color(0xFF6B1530)
val Gold = Color(0xFFD4A437)
val GoldLight = Color(0xFFE8C879)
val Cream = Color(0xFFFFF8E7)
val CreamDark = Color(0xFFF2E7C9)
val Ink = Color(0xFF1A0F12)
val InkMuted = Color(0xFF5F4A50)
val Green = Color(0xFF1B7A3D)
val Red = Color(0xFFD32F2F)
val Amber = Color(0xFFF59E0B)

private val LightColors = lightColorScheme(
    primary = Maroon,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB54665),
    onPrimaryContainer = Color.White,
    secondary = Gold,
    onSecondary = Color(0xFF1A0F12),
    secondaryContainer = GoldLight,
    onSecondaryContainer = Ink,
    tertiary = MaroonDark,
    onTertiary = Color.White,
    background = Cream,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    surfaceVariant = CreamDark,
    onSurfaceVariant = InkMuted,
    outline = InkMuted,
    outlineVariant = Color(0xFFD7C8B0),
    error = Red,
    onError = Color.White
)

private val DarkColors = darkColorScheme(
    primary = Gold,
    onPrimary = Ink,
    primaryContainer = MaroonDark,
    onPrimaryContainer = Color.White,
    secondary = GoldLight,
    onSecondary = Ink,
    secondaryContainer = MaroonDark,
    onSecondaryContainer = Gold,
    tertiary = Gold,
    onTertiary = Ink,
    background = Color(0xFF14090C),
    onBackground = Cream,
    surface = Color(0xFF1F1216),
    onSurface = Cream,
    surfaceVariant = Color(0xFF2C1A20),
    onSurfaceVariant = GoldLight,
    outline = GoldLight,
    outlineVariant = Maroon,
    error = Red,
    onError = Color.White
)

@Composable
fun GoTourNTravelsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = GoTourTypography,
        shapes = GoTourShapes,
        content = content
    )
}
