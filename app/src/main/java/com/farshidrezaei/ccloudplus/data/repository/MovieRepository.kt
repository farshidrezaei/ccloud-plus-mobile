package com.farshidrezaei.ccloudplus.data.repository

import com.farshidrezaei.ccloudplus.core.error.*
import com.farshidrezaei.ccloudplus.data.model.Country
import com.farshidrezaei.ccloudplus.data.model.FilterType
import com.farshidrezaei.ccloudplus.data.model.Genre
import com.farshidrezaei.ccloudplus.data.model.Movie
import com.farshidrezaei.ccloudplus.data.model.Source
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject

class MovieRepository : BaseRepository() {
    private val BASE_URL = com.farshidrezaei.ccloudplus.core.config.ApiConfig.MOVIES_ENDPOINT
    
    suspend fun getMovies(page: Int = 0, genreId: Int = 0, filterType: FilterType = FilterType.DEFAULT): List<Movie> {
        return withContext(Dispatchers.IO) {
            try {
                val url = buildUrl(BASE_URL, genreId, filterType, page)
                val jsonData = executeRequest(url) { Request.Builder().url(it).build() }
                parseMovies(jsonData)
            } catch (e: Exception) {
                if (e is AppError) throw e
                throw ErrorHandler.parseException(e)
            }
        }
    }
    
    private fun buildUrl(baseUrl: String, genreId: Int, filterType: FilterType, page: Int): String {
        return when (filterType) {
            FilterType.DEFAULT -> "$baseUrl/$genreId/created/$page/$API_KEY"
            FilterType.BY_YEAR -> "$baseUrl/$genreId/year/$page/$API_KEY"
            FilterType.BY_IMDB -> "$baseUrl/$genreId/imdb/$page/$API_KEY"
        }
    }
    
    private fun parseMovies(jsonData: String): List<Movie> {
        return try {
            val jsonArray = JSONArray(jsonData)
            jsonArray.mapNotNullSafe { parseMovie(it) }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    private fun parseMovie(movieObj: JSONObject): Movie {
        val durationStr = movieObj.optStringSafe("duration")
            .takeIf { it.isNotBlank() && it != "null" && it != "N/A" }

        return Movie(
            id = movieObj.optIntSafe("id"),
            type = movieObj.optStringSafe("type", "movie"),
            title = movieObj.optStringSafe("title", "بدون عنوان"),
            description = movieObj.optStringSafe("description"),
            year = movieObj.optIntSafe("year"),
            imdb = movieObj.optDoubleSafe("imdb"),
            rating = movieObj.optDoubleSafe("rating"),
            duration = durationStr,
            image = movieObj.optStringSafe("image"),
            cover = movieObj.optStringSafe("cover"),
            genres = movieObj.optArraySafe("genres").mapNotNullSafe {
                Genre(
                    id = it.optIntSafe("id"),
                    title = it.optStringSafe("title")
                )
            },
            sources = movieObj.optArraySafe("sources").mapNotNullSafe {
                Source(
                    id = it.optIntSafe("id"),
                    quality = it.optStringSafe("quality"),
                    type = it.optStringSafe("type"),
                    url = it.optStringSafe("url")
                )
            },
            country = movieObj.optArraySafe("country").mapNotNullSafe {
                Country(
                    id = it.optIntSafe("id"),
                    title = it.optStringSafe("title"),
                    image = it.optStringSafe("image")
                )
            }
        )
    }
}
