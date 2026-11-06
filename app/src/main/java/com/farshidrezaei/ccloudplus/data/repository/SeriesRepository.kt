package com.farshidrezaei.ccloudplus.data.repository

import com.farshidrezaei.ccloudplus.core.error.*
import com.farshidrezaei.ccloudplus.data.model.Country
import com.farshidrezaei.ccloudplus.data.model.FilterType
import com.farshidrezaei.ccloudplus.data.model.Genre
import com.farshidrezaei.ccloudplus.data.model.Series
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject

class SeriesRepository : BaseRepository() {
    private val BASE_URL = com.farshidrezaei.ccloudplus.core.config.ApiConfig.SERIES_ENDPOINT
    
    suspend fun getSeries(page: Int = 0, genreId: Int = 0, filterType: FilterType = FilterType.DEFAULT): List<Series> {
        return withContext(Dispatchers.IO) {
            try {
                val url = buildUrl(BASE_URL, genreId, filterType, page)
                val jsonData = executeRequest(url) { Request.Builder().url(it).build() }
                parseSeries(jsonData)
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
    
    private fun parseSeries(jsonData: String): List<Series> {
        return try {
            val jsonArray = JSONArray(jsonData)
            jsonArray.mapNotNullSafe { parseSeriesItem(it) }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    private fun parseSeriesItem(seriesObj: JSONObject): Series {
        val durationStr = seriesObj.optStringSafe("duration")
            .takeIf { it.isNotBlank() && it != "null" && it != "N/A" }

        return Series(
            id = seriesObj.optIntSafe("id"),
            type = seriesObj.optStringSafe("type", "serie"),
            title = seriesObj.optStringSafe("title", "بدون عنوان"),
            description = seriesObj.optStringSafe("description"),
            year = seriesObj.optIntSafe("year"),
            imdb = seriesObj.optDoubleSafe("imdb"),
            rating = seriesObj.optDoubleSafe("rating"),
            duration = durationStr,
            image = seriesObj.optStringSafe("image"),
            cover = seriesObj.optStringSafe("cover"),
            genres = seriesObj.optArraySafe("genres").mapNotNullSafe {
                Genre(
                    id = it.optIntSafe("id"),
                    title = it.optStringSafe("title")
                )
            },
            country = seriesObj.optArraySafe("country").mapNotNullSafe {
                Country(
                    id = it.optIntSafe("id"),
                    title = it.optStringSafe("title"),
                    image = it.optStringSafe("image")
                )
            }
        )
    }
        val countries = mutableListOf<Country>()
        for (i in 0 until countriesArray.length()) {
            try {
                val countryObj = countriesArray.getJSONObject(i)
                countries.add(
                    Country(
                        id = countryObj.optInt("id", 0),
                        title = countryObj.optString("title", "Unknown"),
                        image = countryObj.optString("image", "")
                    )
                )
            } catch (e: Exception) {
                // Skip countries that fail to parse
                continue
            }
        }
        return countries
    }
}
