package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = VibeRed,
    onPrimary = Color.White,
    primaryContainer = VibeRedDark,
    onPrimaryContainer = Color.White,
    secondary = VibeViolet,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF3B2566),
    onSecondaryContainer = Color(0xFFE9D8FD),
    tertiary = VibeNeonBlue,
    onTertiary = Color.Black,
    background = VibeDarkBackground,
    onBackground = VibeDarkTextPrimary,
    surface = VibeDarkSurface,
    onSurface = VibeDarkTextPrimary,
    surfaceVariant = VibeDarkSurfaceVariant,
    onSurfaceVariant = VibeDarkTextSecondary,
    outline = VibeDarkDivider
)

private val LightColorScheme = lightColorScheme(
    primary = VibeRed,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDADE),
    onPrimaryContainer = VibeRedDark,
    secondary = VibeViolet,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEDE9FE),
    onSecondaryContainer = Color(0xFF2E1065),
    tertiary = VibeNeonBlue,
    onTertiary = Color.White,
    background = VibeLightBackground,
    onBackground = VibeLightTextPrimary,
    surface = VibeLightSurface,
    onSurface = VibeLightTextPrimary,
    surfaceVariant = VibeLightSurfaceVariant,
    onSurfaceVariant = VibeLightTextSecondary,
    outline = VibeLightDivider
)

@Composable
fun VibeTubeTheme(
    darkTheme: Boolean = true, // Default to sleek modern dark theme for video platform
    dynamicColor: Boolean = false, // Preserve brand identity
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    VibeTubeTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
