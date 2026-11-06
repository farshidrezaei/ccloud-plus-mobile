package com.farshidrezaei.ccloudplus.ui.viewmodel

import com.farshidrezaei.ccloudplus.data.model.*
import com.farshidrezaei.ccloudplus.data.repository.SeriesRepository

class SeriesViewModel : BaseMediaListViewModel<Series>() {
    private val repository by lazy { SeriesRepository() }

    init {
        initialize()
    }

    override val featuredItems: List<Movie>
        get() = allItems.filter { it.cover.isNotBlank() || it.image.isNotBlank() }.take(6).map { it.toMovie() }

    val seriesList: List<Series>
        get() = filteredItems.ifEmpty { allItems }

    override suspend fun fetchItems(page: Int, genreId: Int, filterType: FilterType): List<Series> {
        return repository.getSeries(page = page, genreId = genreId, filterType = filterType)
    }

    override fun matchesFilter(item: Series, filter: AdvancedFilter): Boolean {
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
