package com.example.akshara_deepatutor.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFD1E4FF), // Light blue for dark mode visibility
    secondary = BlueSecondary,
    tertiary = Color(0xFFA5C8FF),
    background = Color(0xFF1A1C1E),
    surface = Color(0xFF1A1C1E),
    onPrimary = Color(0xFF003061),
    onSecondary = Color(0xFF003258),
    onBackground = Color(0xFFE2E2E6),
    onSurface = Color(0xFFE2E2E6)
)

private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    secondary = BlueSecondary,
    tertiary = BlueTertiary,
    background = BackgroundWhite,
    surface = BackgroundWhite,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    primaryContainer = BlueTertiary,
    onPrimaryContainer = BluePrimary
)

@Composable
fun AksharaDeepaTutorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        val window = (view.context as Activity).window
        window.statusBarColor = colorScheme.background.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
