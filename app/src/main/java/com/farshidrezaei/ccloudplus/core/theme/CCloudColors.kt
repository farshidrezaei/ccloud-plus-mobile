package com.farshidrezaei.ccloudplus.core.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object CCloudColors {
    // OLED pure background
    val BackgroundOled = Color(0xFF090A0E)
    val BackgroundPureBlack = Color(0xFF000000)
    val SurfaceDark = Color(0xFF13151B)
    
    // Light mode backgrounds
    val BackgroundLight = Color(0xFFF8F9FA)
    val SurfaceLight = Color(0xFFFFFFFF)
    
    // Glassmorphism surfaces
    val GlassSurface = Color(0x14FFFFFF)           // 8% white
    val GlassSurfaceElevated = Color(0x24FFFFFF)   // 14% white
    val GlassSurfaceHover = Color(0x38FFFFFF)      // 22% white
    val GlassBorder = Color(0x1FFFFFFF)            // 12% white subtle border
    val GlassBorderFocus = Color(0xFFFFFFFF)       // White focus border for TV
    
    // Signature Streaming Red (CCloud+ Flagship style)
    val AccentRed = Color(0xFFE50914)
    val AccentRedHover = Color(0xFFFF2E36)
    val AccentRedGlow = Color(0x66E50914)
    
    // Text tokens
    val TextPrimary = Color(0xFFF8FAFC)
    val TextSecondary = Color(0xFF94A3B8)
    val TextMuted = Color(0xFF64748B)
    
    // Badges & States
    val RatingGold = Color(0xFFFFB800)
    val SuccessGreen = Color(0xFF22C55E)
    val ErrorRed = Color(0xFFEF4444)
    val InfoBlue = Color(0xFF38BDF8)
    
    // Gradients
    val HeroVignette = Brush.verticalGradient(
        colors = listOf(
            Color.Transparent,
            Color(0x99090A0E),
            BackgroundOled
        )
    )
    
    val HeroHorizontalVignette = Brush.horizontalGradient(
        colors = listOf(
            BackgroundOled,
            Color(0x66090A0E),
            Color.Transparent
        )
    )
    
    val GlassCardBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0x2EFFFFFF),
            Color(0x0FFFFFFF)
        )
    )
}

// Typealias to migrate away from Cinevex gracefully without breakages
typealias CinevexColors = CCloudColors
