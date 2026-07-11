package com.gotourntravels.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Minimal neutral palette with a single blue brand accent.
val Maroon = Color(0xFF1565D8) // Kept as an alias while screens migrate to semantic theme colours.
val MaroonDark = Color(0xFF0B3B82)
val Gold = Color(0xFF1565D8)
val GoldLight = Color(0xFFDCEBFF)
val Cream = Color(0xFFF8FAFC)
val CreamDark = Color(0xFFEAF0F7)
val Ink = Color(0xFF101828)
val InkMuted = Color(0xFF667085)
val Green = Color(0xFF147A52)
val Red = Color(0xFFB42318)
val Amber = Color(0xFF1565D8)

private val LightColors = lightColorScheme(
    primary = Maroon,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCEBFF),
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
    outlineVariant = Color(0xFFD0D5DD),
    error = Red,
    onError = Color.White
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF9CC5FF),
    onPrimary = Color(0xFF062B60),
    primaryContainer = Color(0xFF0B3B82),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF9CC5FF),
    onSecondary = Color(0xFF062B60),
    secondaryContainer = Color(0xFF0B3B82),
    onSecondaryContainer = Color(0xFFDCEBFF),
    tertiary = Color(0xFF9CC5FF),
    onTertiary = Color(0xFF062B60),
    background = Color(0xFF0B0F17),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF121926),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF1D2939),
    onSurfaceVariant = Color(0xFF98A2B3),
    outline = Color(0xFF98A2B3),
    outlineVariant = Color(0xFF344054),
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
