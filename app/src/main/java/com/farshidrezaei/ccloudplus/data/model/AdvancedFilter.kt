package com.farshidrezaei.ccloudplus.data.model

data class AdvancedFilter(
    val minImdb: Float = 0f,
    val era: ReleaseEra = ReleaseEra.ALL,
    val audioType: AudioType = AudioType.ALL
)

enum class ReleaseEra(val titleFa: String) {
    ALL("همه سال‌ها"),
    Y2024_PLUS("۲۰۲۴ به بعد"),
    Y2020_2023("۲۰۲۰ تا ۲۰۲۳"),
    Y2010_2019("۲۰۱۰ تا ۲۰۱۹"),
    CLASSIC("کلاسیک (قبل از ۲۰۱۰)")
}

enum class AudioType(val titleFa: String) {
    ALL("همه"),
    DUBBED("دوبله فارسی"),
    SUBTITLED("زیرنویس چسبیده")
}
