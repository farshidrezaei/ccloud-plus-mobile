package com.farshidrezaei.ccloudplus.core.config

object ApiConfig {
    const val API_KEY = "4F5A9C3D9A86FA54EACEDDD635185"
    
    const val PRIMARY_BASE_URL = "https://server-hi-speed-iran.info/api"
    
    val HELPER_SERVERS = listOf(
        "https://hostinnegar.com",
        "https://windowsdiba.info"
    )
    
    const val CONNECT_TIMEOUT_SECONDS = 30L
    const val READ_TIMEOUT_SECONDS = 30L
    const val WRITE_TIMEOUT_SECONDS = 30L
    
    // Endpoints
    const val MOVIES_ENDPOINT = "$PRIMARY_BASE_URL/movie/by/filtres"
    const val SERIES_ENDPOINT = "$PRIMARY_BASE_URL/serie/by/filtres"
    const val GENRES_ENDPOINT = "$PRIMARY_BASE_URL/genre/all"
    const val COUNTRIES_ENDPOINT = "$PRIMARY_BASE_URL/country/all"
    const val SEARCH_ENDPOINT = "$PRIMARY_BASE_URL/search"
    const val SEASONS_ENDPOINT = "$PRIMARY_BASE_URL/season/by/serie"
}
