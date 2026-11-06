package com.farshidrezaei.ccloudplus.core.config

object AppConstants {
    const val APP_NAME = "CCloud+"
    const val APP_FULL_NAME = "CCloud Plus"
    const val APP_VERSION = "2.0.0"
    
    // Video player constants
    const val MAX_VIDEO_CACHE_SIZE_BYTES = 500L * 1024 * 1024 // 500 MB
    const val AUTO_NEXT_COUNTDOWN_SECONDS = 15
    const val DEFAULT_BUFFER_MIN_MS = 15000
    const val DEFAULT_BUFFER_MAX_MS = 50000
    const val DEFAULT_BUFFER_FOR_PLAYBACK_MS = 2500
    const val DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS = 5000
    
    // UI constants
    const val HERO_CAROUSEL_AUTO_SCROLL_SECONDS = 7L
    const val DOUBLE_TAP_SEEK_SECONDS = 10
    
    // Preferences keys
    const val PREFS_NAME = "ccloud_plus_prefs"
    const val KEY_DNS_PRESET = "key_dns_preset"
    const val KEY_SUBTITLE_SETTINGS = "key_subtitle_settings"
    const val KEY_FONT_SETTINGS = "key_font_settings"
    const val KEY_THEME_MODE = "key_theme_mode"
    const val KEY_AUTO_NEXT_ENABLED = "key_auto_next_enabled"
    const val KEY_HARDWARE_ACCELERATION = "key_hardware_acceleration"
}
