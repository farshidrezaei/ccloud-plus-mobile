package com.farshidrezaei.ccloudplus.player

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.farshidrezaei.ccloudplus.core.config.ApiConfig
import com.farshidrezaei.ccloudplus.core.config.AppConstants
import java.io.File

@OptIn(UnstableApi::class)
object ExoPlayerManager {
    @Volatile
    private var simpleCache: SimpleCache? = null

    private fun getSimpleCache(context: Context): SimpleCache {
        return simpleCache ?: synchronized(this) {
            simpleCache ?: run {
                val cacheDir = File(context.cacheDir, "ccloud_video_cache")
                val evictor = LeastRecentlyUsedCacheEvictor(AppConstants.MAX_VIDEO_CACHE_SIZE_BYTES)
                val databaseProvider = StandaloneDatabaseProvider(context.applicationContext)
                SimpleCache(cacheDir, evictor, databaseProvider).also { simpleCache = it }
            }
        }
    }

    fun buildCacheDataSourceFactory(context: Context): DataSource.Factory {
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setConnectTimeoutMs((ApiConfig.CONNECT_TIMEOUT_SECONDS * 1000).toInt())
            .setReadTimeoutMs((ApiConfig.READ_TIMEOUT_SECONDS * 1000).toInt())
            .setAllowCrossProtocolRedirects(true)
            
        val defaultDataSourceFactory = DefaultDataSource.Factory(context.applicationContext, httpDataSourceFactory)
        val cache = getSimpleCache(context)

        return CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(defaultDataSourceFactory)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }

    fun createOptimizedPlayer(context: Context): ExoPlayer {
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                AppConstants.DEFAULT_BUFFER_MIN_MS,
                AppConstants.DEFAULT_BUFFER_MAX_MS,
                AppConstants.DEFAULT_BUFFER_FOR_PLAYBACK_MS,
                AppConstants.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
            )
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()

        val dataSourceFactory = buildCacheDataSourceFactory(context)
        val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)

        return ExoPlayer.Builder(context.applicationContext)
            .setMediaSourceFactory(mediaSourceFactory)
            .setLoadControl(loadControl)
            .setSeekForwardIncrementMs(10000)
            .setSeekBackIncrementMs(10000)
            .build()
    }

    fun releaseCache() {
        synchronized(this) {
            try {
                simpleCache?.release()
                simpleCache = null
            } catch (ignored: Exception) {}
        }
    }
}
