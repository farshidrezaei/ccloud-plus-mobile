package com.farshidrezaei.ccloudplus.core.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val CCloudDarkColorScheme = darkColorScheme(
    primary = CCloudColors.AccentRed,
    onPrimary = Color.White,
    primaryContainer = CCloudColors.AccentRed,
    onPrimaryContainer = Color.White,
    secondary = CCloudColors.AccentRed,
    onSecondary = Color.White,
    background = Color(0xFF090A0E),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF13151B),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF1C1E26),
    onSurfaceVariant = Color(0xFF94A3B8)
)

private val CCloudLightColorScheme = lightColorScheme(
    primary = CCloudColors.AccentRed,
    onPrimary = Color.White,
    primaryContainer = CCloudColors.AccentRed,
    onPrimaryContainer = Color.White,
    secondary = CCloudColors.AccentRed,
    onSecondary = Color.White,
    background = Color(0xFFF8F9FA),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569)
)

@Composable
fun CCloudTheme(
    content: @Composable () -> Unit
) {
    val isDark = when (ThemeManager.currentThemeMode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.DARK -> true
        AppThemeMode.LIGHT -> false
    }

    val palette = if (isDark) DarkColorPalette else LightColorPalette
    val colorScheme = if (isDark) CCloudDarkColorScheme else CCloudLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !isDark
            insetsController.isAppearanceLightNavigationBars = !isDark
        }
    }

    CompositionLocalProvider(LocalCCloudColors provides palette) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}
