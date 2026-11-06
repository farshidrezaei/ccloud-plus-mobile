package com.farshidrezaei.ccloudplus.core.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

data class CCloudColorPalette(
    val background: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val surfaceCard: Color,
    val glassBorder: Color,
    val glassBorderFocus: Color,
    val accentRed: Color = Color(0xFFE50914),
    val accentRedHover: Color = Color(0xFFFF2E36),
    val accentRedGlow: Color = Color(0x66E50914),
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val ratingGold: Color = Color(0xFFFFB800),
    val heroVignette: Brush,
    val isDark: Boolean
)

val DarkColorPalette = CCloudColorPalette(
    background = Color(0xFF090A0E),
    surface = Color(0xFF13151B),
    surfaceElevated = Color(0x24FFFFFF),
    surfaceCard = Color(0x14FFFFFF),
    glassBorder = Color(0x1FFFFFFF),
    glassBorderFocus = Color(0xFFFFFFFF),
    textPrimary = Color(0xFFF8FAFC),
    textSecondary = Color(0xFF94A3B8),
    textMuted = Color(0xFF64748B),
    heroVignette = Brush.verticalGradient(
        colors = listOf(
            Color.Transparent,
            Color(0x99090A0E),
            Color(0xFF090A0E)
        )
    ),
    isDark = true
)

val LightColorPalette = CCloudColorPalette(
    background = Color(0xFFF8F9FA),
    surface = Color(0xFFFFFFFF),
    surfaceElevated = Color(0xFFF1F5F9),
    surfaceCard = Color(0xFFFFFFFF),
    glassBorder = Color(0x14000000),
    glassBorderFocus = Color(0xFFE50914),
    textPrimary = Color(0xFF0F172A),
    textSecondary = Color(0xFF475569),
    textMuted = Color(0xFF94A3B8),
    heroVignette = Brush.verticalGradient(
        colors = listOf(
            Color.Transparent,
            Color(0x80F8F9FA),
            Color(0xFFF8F9FA)
        )
    ),
    isDark = false
)

val LocalCCloudColors = staticCompositionLocalOf { DarkColorPalette }

object CCloudTheme {
    val colors: CCloudColorPalette
        @Composable
        get() = LocalCCloudColors.current

    val typography: CCloudTypography
        get() = CCloudTypography
}

object CCloudColors {
    // Dynamic accessors tied to Compose theme
    val Background: Color @Composable get() = CCloudTheme.colors.background
    val Surface: Color @Composable get() = CCloudTheme.colors.surface
    val SurfaceElevated: Color @Composable get() = CCloudTheme.colors.surfaceElevated
    val TextPrimary: Color @Composable get() = CCloudTheme.colors.textPrimary
    val TextSecondary: Color @Composable get() = CCloudTheme.colors.textSecondary
    val TextMuted: Color @Composable get() = CCloudTheme.colors.textMuted
    val GlassBorder: Color @Composable get() = CCloudTheme.colors.glassBorder
    val GlassSurface: Color @Composable get() = CCloudTheme.colors.surfaceCard

    // Constant signature colors
    val AccentRed = Color(0xFFE50914)
    val AccentRedHover = Color(0xFFFF2E36)
    val AccentRedGlow = Color(0x66E50914)
    val RatingGold = Color(0xFFFFB800)
    val SuccessGreen = Color(0xFF22C55E)
    val ErrorRed = Color(0xFFEF4444)

    // Fallback static constants for non-composable scopes
    val BackgroundOled = Color(0xFF090A0E)
    val BackgroundLight = Color(0xFFF8F9FA)
    val SurfaceDark = Color(0xFF13151B)
    val SurfaceLight = Color(0xFFFFFFFF)
    val GlassSurfaceElevated = Color(0x24FFFFFF)
    val GlassSurfaceHover = Color(0x38FFFFFF)
    val GlassBorderFocus = Color(0xFFFFFFFF)
    val HeroVignette = Brush.verticalGradient(
        listOf(Color.Transparent, Color(0x99090A0E), Color(0xFF090A0E))
    )
}
