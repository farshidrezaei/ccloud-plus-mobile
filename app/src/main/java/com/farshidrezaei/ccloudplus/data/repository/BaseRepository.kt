package com.farshidrezaei.ccloudplus.data.repository

import com.farshidrezaei.ccloudplus.core.config.ApiConfig
import com.farshidrezaei.ccloudplus.core.error.AppError
import com.farshidrezaei.ccloudplus.core.error.ErrorHandler
import com.farshidrezaei.ccloudplus.core.network.OkHttpClientProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request

open class BaseRepository {
    protected val client get() = OkHttpClientProvider.getClient()
    
    protected val API_KEY = ApiConfig.API_KEY
    protected val helperServers = ApiConfig.HELPER_SERVERS
    
    protected suspend fun executeRequest(
        primaryUrl: String,
        requestBuilder: (String) -> Request
    ): String {
        return withContext(Dispatchers.IO) {
            var lastException: Throwable? = null
            
            // 1. Try the primary server
            try {
                val primaryRequest = requestBuilder(primaryUrl)
                client.newCall(primaryRequest).execute().use { response ->
                    if (response.isSuccessful) {
                        return@withContext response.body?.string()
                            ?: throw AppError.Parse()
                    } else {
                        throw AppError.Server(response.code)
                    }
                }
            } catch (primaryException: Throwable) {
                lastException = primaryException
                
                // 2. If primary fails, failover to helper servers
                for (helperServer in helperServers) {
                    try {
                        val helperUrl = primaryUrl.replace(Regex("^https?://[^/]+"), helperServer)
                        val helperRequest = requestBuilder(helperUrl)
                        client.newCall(helperRequest).execute().use { response ->
                            if (response.isSuccessful) {
                                return@withContext response.body?.string()
                                    ?: throw AppError.Parse()
                            }
                        }
                    } catch (helperException: Throwable) {
                        lastException = helperException
                        continue
                    }
                }
            }
            
            // If all servers fail, translate error into clean AppError
            val error = ErrorHandler.parseException(lastException ?: Exception("Network request failed"))
            throw error
        }
    }
}
