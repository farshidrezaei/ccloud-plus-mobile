<p align="center">
  <img src="app/src/main/res/drawable/app_logo.png" alt="CCloud+ Logo" width="160">
</p>

<h1 align="center">CCloud+ (CCloud Plus) Mobile & Android TV</h1>

<p align="center">
  <b>A modernized, high-performance, cinematic mobile and Android TV streaming powerhouse for movies and series with Persian subtitles and dubbed audio.</b>
</p>

<div align="center">

  <img src="https://img.shields.io/badge/Kotlin-2.0-blue?style=for-the-badge&logo=kotlin" alt="Kotlin">
  <img src="https://img.shields.io/badge/Jetpack%20Compose-OLED%20Glass-purple?style=for-the-badge&logo=android" alt="Jetpack Compose">
  <img src="https://img.shields.io/badge/Platform-Android%20Mobile%20%7C%20Tablet%20%7C%20TV-green?style=for-the-badge&logo=android" alt="Android">
  <img src="https://img.shields.io/badge/License-MIT-red.svg?style=for-the-badge" alt="License: MIT">
  <a href="https://github.com/code3-dev/CCloud"><img src="https://img.shields.io/badge/Forked%20From-CCloud-purple?style=for-the-badge&logo=github" alt="Upstream CCloud"></a>

</div>

---

## 🌟 Upstream Attribution & Heartfelt Gratitude

> ### 💖 Standing on the Shoulders of Giants
> 
> **CCloud+ (CCloud Plus)** is an independent, advanced evolution built upon the remarkable open-source foundation of [**CCloud**](https://github.com/code3-dev/CCloud), originally crafted by [**Hossein Pira (code3-dev)**](https://github.com/code3-dev).
> 
> We extend our deepest respect, admiration, and gratitude to **Hossein Pira** and all original contributors for their vision and dedication to open-source software for the Persian community. Since the original repository development ceased, **CCloud+** preserves this open spirit while pushing the boundaries of mobile and TV streaming performance, clean architecture, usability, and design.

---

## ✨ Why CCloud+? (Feature Comparison)

| Capability / Feature | Original CCloud | CCloud+ (Plus) 🎬 |
|:---|:---:|:---:|
| **Design System** | Classic Material 3 | **Cinevex Glassmorphic OLED UI** (Non-Material) |
| **Hero Spotlight Carousel** | ❌ | **✅ Auto-rotating 7s cinematic spotlight banner** |
| **Android TV Navigation** | Basic Leanback | **✅ D-pad reactive focus engine + 1.08x neon scale** |
| **Media Caching** | Direct Streaming Only | **✅ 500MB LRU Disk Cache for instant seeking & offline** |
| **Picture-in-Picture (PiP)** | ❌ | **✅ Native Android OS PiP mode (`enterPictureInPictureMode`)** |
| **Live Subtitle Syncer** | Styling only | **✅ Live `±0.1s` to `±1.0s` delay tuning + local file picker** |
| **Binge Tracker & Auto-Next** | Watched history only | **✅ 15-second floating auto-next countdown banner** |
| **Quick Peek Modal** | ❌ | **✅ Long-press instant preview bottom sheet** |
| **Multi-Filter Sheet** | Basic Sort | **✅ IMDb slider, decade era, and audio/dubbed filters** |
| **Anti-Sanction DNS** | Hardcoded failover only | **✅ Built-in DoH & Anti-Sanction DNS switcher (Shecan, 403, Electro, Radar)** |

---

## 🚀 Key Features

### 1. 🎨 Cinevex Glassmorphism OLED Theme (Non-Material)
- **Pure Black OLED Aesthetic**: Pitch-black background (`#090A0E`) with `#E50914` signature Netflix-inspired red accents.
- **Translucent Glass Surfaces**: Frosted acrylic containers with delicate 1px border (`rgba(255,255,255,0.08)`).
- **Responsive 3-Way Layout**:
  - **Mobile**: Floating Glass Navigation Dock at the bottom.
  - **Tablet**: Adaptive 2-pane / wide grid view.
  - **Android TV (10-Foot UI)**: Full D-pad remote support with glowing focus rings and auto-scaling cards.

### 2. 🎠 Featured Hero Spotlight Carousel
- Smooth auto-advancing banner highlighting top trending movies and series every 7 seconds.
- Cinema vignette gradient backdrop, IMDb rating badges, genres, plot overview, and quick action triggers (*Play Now* & *Details*).

### 3. 🛡️ Anti-Sanction DNS Bypass & Smart Persian Diagnostic
- In-app **DNS over HTTPS (DoH)** switcher directly inside OkHttp.
- 1-click bypass presets for:
  - **Shecan (شکن)**
  - **403.online (سامانه ۴۰۳)**
  - **Electro (الکترو)**
  - **Radar Game (رادار)**
  - **Cloudflare & Google DoH**
- Translates cryptic network, SSL, and timeout errors into clear, guided Persian diagnostics with automatic retry.

### 4. ⚡ Next-Gen ExoPlayer Engine
- **500MB LRU Media Cache**: Videos are cached to disk as they stream. Seeking backwards or replaying does not re-download data.
- **Custom Buffer Control**: Fine-tuned for unstable network connections with quick startup.
- **Native Picture-in-Picture (PiP)**: Keep watching while browsing or using other apps.
- **Live Subtitle Syncer**: Real-time delay adjustment (`-1.0s` to `+1.0s`) and custom local `.srt` loader.
- **Binge Auto-Next**: 15-second floating countdown banner to smoothly start the next episode.

---

## 🏗️ Project Architecture

```
com.farshidrezaei.ccloudplus/
├── core/
│   ├── config/           # ApiConfig, AppConstants
│   ├── error/            # AppError, AppResult, ErrorHandler
│   ├── network/          # OkHttpClientProvider, DnsBypass, DnsPreset
│   ├── theme/            # CinevexColors, CinevexTypography (Vazirmatn)
│   └── utils/            # DeviceUtils, StorageUtils, DownloadUtils
├── data/
│   ├── model/            # Movie, Series, Genre, AdvancedFilter, DTOs
│   └── repository/       # BaseRepository, MovieRepository, SeriesRepository
├── player/
│   ├── ExoPlayerManager.kt  # 500MB Cache, Buffering & MediaSource Factory
│   └── VideoPlayerActivity.kt # PiP, Gestures, Subtitle Sync, Auto-Next
└── ui/
    ├── components/       # GlassBox, GlassButton, MediaCard, FeaturedHeroCarousel, AdvancedFilterSheet
    ├── navigation/       # AppNavigation, GlassBottomNavigation, GlassSidebarNavigation
    ├── screens/          # Movies, Series, Downloads, Favorites, Settings, Search, SingleMovie/Series
    └── viewmodel/        # MoviesViewModel, SeriesViewModel, SearchViewModel
```

---

## 🛠️ Tech Stack

- **Language**: Kotlin 2.0
- **UI Toolkit**: Jetpack Compose (Cinevex Glassmorphism OLED System)
- **Media Engine**: AndroidX Media3 ExoPlayer 1.4.1 (with `SimpleCache` & `DefaultLoadControl`)
- **Networking**: OkHttp 4.12.0 + OkHttp DNS over HTTPS
- **Image Loading**: Coil 2.7.0
- **Serialization**: Kotlinx Serialization JSON
- **Typography**: Vazirmatn Font Family

---

## 📄 License & Credits

- Upstream Project: [CCloud](https://github.com/code3-dev/CCloud) by [Hossein Pira (code3-dev)](https://github.com/code3-dev).
- Evolution & Modernization: **Farshid Rezaei**.
- Released under the [MIT License](LICENSE).
