package com.farshidrezaei.ccloudplus.core.network

import okhttp3.Dns
import java.net.InetAddress
import java.net.UnknownHostException

enum class DnsPreset(
    val titleFa: String,
    val primaryIp: String?,
    val secondaryIp: String?,
    val isDoh: Boolean = false,
    val dohUrl: String? = null
) {
    SYSTEM("پیش‌فرض سیستم", null, null),
    SHECAN("شکن (Shecan)", "178.22.122.100", "185.51.200.2"),
    ONLINE_403("سامانه ۴۰۳ (403.online)", "10.202.10.202", "10.202.10.102"),
    ELECTRO("الکترو (Electro)", "78.157.42.101", "78.157.42.100"),
    RADAR("رادار گیم (Radar)", "10.202.10.10", "10.202.10.11"),
    CLOUDFLARE("کلادفلر (Cloudflare DoH)", "1.1.1.1", "1.0.0.1", isDoh = true, dohUrl = "https://cloudflare-dns.com/dns-query"),
    GOOGLE("گوگل (Google DoH)", "8.8.8.8", "8.8.4.4", isDoh = true, dohUrl = "https://dns.google/dns-query")
}

class CustomDns(private val preset: DnsPreset) : Dns {
    override fun lookup(hostname: String): List<InetAddress> {
        if (preset == DnsPreset.SYSTEM || preset.primaryIp == null) {
            return Dns.SYSTEM.lookup(hostname)
        }
        
        return try {
            val addresses = mutableListOf<InetAddress>()
            preset.primaryIp?.let { ip ->
                addresses.add(InetAddress.getByAddress(hostname, InetAddress.getByName(ip).address))
            }
            preset.secondaryIp?.let { ip ->
                addresses.add(InetAddress.getByAddress(hostname, InetAddress.getByName(ip).address))
            }
            if (addresses.isEmpty()) {
                Dns.SYSTEM.lookup(hostname)
            } else {
                addresses
            }
        } catch (e: Exception) {
            // Fallback to system DNS
            Dns.SYSTEM.lookup(hostname)
        }
    }
}
