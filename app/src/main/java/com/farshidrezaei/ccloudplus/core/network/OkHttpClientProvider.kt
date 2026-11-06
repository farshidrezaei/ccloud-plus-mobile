package com.farshidrezaei.ccloudplus.core.network

import com.farshidrezaei.ccloudplus.core.config.ApiConfig
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.dnsoverhttps.DnsOverHttps
import java.net.InetAddress
import java.util.concurrent.TimeUnit

object OkHttpClientProvider {
    @Volatile
    private var currentPreset: DnsPreset = DnsPreset.SYSTEM
    
    @Volatile
    private var clientInstance: OkHttpClient? = null

    fun getClient(): OkHttpClient {
        return clientInstance ?: synchronized(this) {
            clientInstance ?: buildClient(currentPreset).also { clientInstance = it }
        }
    }

    fun setDnsPreset(preset: DnsPreset) {
        if (currentPreset != preset) {
            synchronized(this) {
                currentPreset = preset
                clientInstance = buildClient(preset)
            }
        }
    }

    fun getCurrentPreset(): DnsPreset = currentPreset

    private fun buildClient(preset: DnsPreset): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(ApiConfig.CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(ApiConfig.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(ApiConfig.WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)

        if (preset.isDoh && preset.dohUrl != null) {
            val bootstrapClient = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build()

            val doh = DnsOverHttps.Builder()
                .client(bootstrapClient)
                .url(preset.dohUrl.toHttpUrl())
                .bootstrapDnsHosts(
                    InetAddress.getByName(preset.primaryIp ?: "1.1.1.1"),
                    InetAddress.getByName(preset.secondaryIp ?: "1.0.0.1")
                )
                .build()

            builder.dns(doh)
        } else if (preset != DnsPreset.SYSTEM) {
            builder.dns(CustomDns(preset))
        }

        return builder.build()
    }
}
