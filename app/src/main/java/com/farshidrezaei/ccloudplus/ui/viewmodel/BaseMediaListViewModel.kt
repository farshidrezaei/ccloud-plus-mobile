package com.farshidrezaei.ccloudplus.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farshidrezaei.ccloudplus.core.error.AppError
import com.farshidrezaei.ccloudplus.core.error.ErrorHandler
import com.farshidrezaei.ccloudplus.data.model.*
import com.farshidrezaei.ccloudplus.data.repository.GenreRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

abstract class BaseMediaListViewModel<T> : ViewModel() {
    private val genreRepository = GenreRepository()

    var allItems by mutableStateOf<List<T>>(emptyList())
        protected set

    var filteredItems by mutableStateOf<List<T>>(emptyList())
        protected set

    var isLoading by mutableStateOf(false)
        protected set

    var isLoadingMore by mutableStateOf(false)
        protected set

    var error by mutableStateOf<AppError?>(null)
        protected set

    var currentPage by mutableStateOf(0)
        protected set

    var canLoadMore by mutableStateOf(true)
        protected set

    var genres by mutableStateOf<List<Genre>>(emptyList())
        protected set

    var selectedGenreId by mutableStateOf(0)
        protected set

    var selectedFilterType by mutableStateOf(FilterType.DEFAULT)
        protected set

    var advancedFilter by mutableStateOf(AdvancedFilter())
        protected set

    abstract val featuredItems: List<Movie>

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        error = ErrorHandler.parseException(throwable)
        isLoading = false
        isLoadingMore = false
    }

    init {
        loadGenres()
        loadItems()
    }

    private fun loadGenres() {
        viewModelScope.launch(exceptionHandler) {
            try {
                genres = genreRepository.getGenres()
            } catch (ignored: Throwable) {}
        }
    }

    fun selectGenre(genreId: Int) {
        if (selectedGenreId != genreId) {
            selectedGenreId = genreId
            refresh()
        }
    }

    fun selectFilterType(filterType: FilterType) {
        if (selectedFilterType != filterType) {
            selectedFilterType = filterType
            refresh()
        }
    }

    fun applyAdvancedFilter(filter: AdvancedFilter) {
        advancedFilter = filter
        applyFilterInternal()
    }

    protected abstract suspend fun fetchItems(page: Int, genreId: Int, filterType: FilterType): List<T>
    protected abstract fun matchesFilter(item: T, filter: AdvancedFilter): Boolean

    private fun applyFilterInternal() {
        filteredItems = allItems.filter { matchesFilter(it, advancedFilter) }
    }

    fun loadItems() {
        if (isLoading || isLoadingMore || !canLoadMore) return

        viewModelScope.launch(exceptionHandler) {
            if (currentPage == 0) {
                isLoading = true
            } else {
                isLoadingMore = true
            }
            error = null

            try {
                val newItems = fetchItems(currentPage, selectedGenreId, selectedFilterType)
                if (newItems.isEmpty()) {
                    canLoadMore = false
                } else {
                    if (currentPage == 0) {
                        allItems = newItems
                    } else {
                        val currentList = allItems.toMutableList()
                        currentList.addAll(newItems)
                        allItems = currentList
                    }
                    applyFilterInternal()
                    currentPage++
                }
            } catch (e: AppError) {
                error = e
            } catch (e: Throwable) {
                error = ErrorHandler.parseException(e)
            } finally {
                isLoading = false
                isLoadingMore = false
            }
        }
    }

    fun loadMore() {
        if (canLoadMore && !isLoading && !isLoadingMore) {
            loadItems()
        }
    }

    fun refresh() {
        currentPage = 0
        canLoadMore = true
        allItems = emptyList()
        filteredItems = emptyList()
        loadItems()
    }

    fun retry() {
        loadItems()
    }
}
