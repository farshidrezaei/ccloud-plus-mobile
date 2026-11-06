package com.farshidrezaei.ccloudplus.data.repository

import com.farshidrezaei.ccloudplus.data.model.Genre
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject

class GenreRepository : BaseRepository() {
    private val GENRE_URL = com.farshidrezaei.ccloudplus.core.config.ApiConfig.GENRES_ENDPOINT
    
    suspend fun getGenres(): List<Genre> {
        return withContext(Dispatchers.IO) {
            try {
                val url = "$GENRE_URL/$API_KEY"
                
                val jsonData = executeRequest(url) { Request.Builder().url(it).build() }
                
                parseGenres(jsonData)
            } catch (e: Exception) {
                throw Exception("Error fetching genres: ${e.message}")
            }
        }
    }
    
    private fun parseGenres(jsonData: String): List<Genre> {
        val genres = mutableListOf<Genre>()
        val jsonArray = JSONArray(jsonData)
        
        for (i in 0 until jsonArray.length()) {
            try {
                val genreObj = jsonArray.getJSONObject(i)
                val genre = Genre(
                    id = genreObj.optInt("id", 0),
                    title = genreObj.optString("title", "Unknown")
                )
                genres.add(genre)
            } catch (e: Exception) {
                // Skip genres that fail to parse
                continue
            }
        }
        
        return genres.sortedBy { it.title }
    }
}