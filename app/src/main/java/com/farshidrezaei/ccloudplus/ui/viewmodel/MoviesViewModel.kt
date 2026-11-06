package com.farshidrezaei.ccloudplus.ui.viewmodel

import com.farshidrezaei.ccloudplus.data.model.*
import com.farshidrezaei.ccloudplus.data.repository.MovieRepository

class MoviesViewModel : BaseMediaListViewModel<Movie>() {
    private val repository by lazy { MovieRepository() }

    init {
        initialize()
    }

    override val featuredItems: List<Movie>
        get() = allItems.filter { it.cover.isNotBlank() || it.image.isNotBlank() }.take(6)

    val movies: List<Movie>
        get() = filteredItems.ifEmpty { allItems }

    override suspend fun fetchItems(page: Int, genreId: Int, filterType: FilterType): List<Movie> {
        return repository.getMovies(page = page, genreId = genreId, filterType = filterType)
    }

    override fun matchesFilter(item: Movie, filter: AdvancedFilter): Boolean {
        if (filter.minImdb > 0f && item.imdb < filter.minImdb) return false
        return when (filter.era) {
            ReleaseEra.Y2024_PLUS -> item.year >= 2024
            ReleaseEra.Y2020_2023 -> item.year in 2020..2023
            ReleaseEra.Y2010_2019 -> item.year in 2010..2019
            ReleaseEra.CLASSIC -> item.year < 2010
            ReleaseEra.ALL -> true
        }
    }
}
