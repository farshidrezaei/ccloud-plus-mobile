package com.farshidrezaei.ccloudplus.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ContinueWatchingItem(
    val id: Int,
    val type: String, // "movie" or "series"
    val title: String,
    val poster: String,
    val videoUrl: String,
    val positionMs: Long,
    val durationMs: Long,
    val seriesId: Int = 0,
    val seasonId: Int = 0,
    val episodeId: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
) {
    val progressFloat: Float
        get() = if (durationMs > 0) (positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f
}
