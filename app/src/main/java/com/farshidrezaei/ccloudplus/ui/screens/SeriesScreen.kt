package com.farshidrezaei.ccloudplus.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.farshidrezaei.ccloudplus.core.theme.CinevexColors
import com.farshidrezaei.ccloudplus.core.theme.CinevexTypography
import com.farshidrezaei.ccloudplus.core.utils.DeviceUtils
import com.farshidrezaei.ccloudplus.core.utils.StorageUtils
import com.farshidrezaei.ccloudplus.data.model.Movie
import com.farshidrezaei.ccloudplus.data.model.Series
import com.farshidrezaei.ccloudplus.data.model.toFavoriteItem
import com.farshidrezaei.ccloudplus.ui.components.*
import com.farshidrezaei.ccloudplus.ui.navigation.AppScreens
import com.farshidrezaei.ccloudplus.ui.viewmodel.SeriesViewModel

@Composable
fun SeriesScreen(
    navController: NavController,
    viewModel: SeriesViewModel = viewModel()
) {
    val context = LocalContext.current
    val isTv = remember { DeviceUtils.isTv(context) }

    val seriesList = viewModel.seriesList
    val featuredSeries = viewModel.featuredItems
    val isLoading = viewModel.isLoading
    val isLoadingMore = viewModel.isLoadingMore
    val error = viewModel.error
    val genres = viewModel.genres
    val selectedGenreId = viewModel.selectedGenreId

    var previewSeries by remember { mutableStateOf<Series?>(null) }
    var showFilterSheet by remember { mutableStateOf(false) }

    val gridColumns = remember(isTv) {
        if (isTv) GridCells.Fixed(5) else GridCells.Adaptive(minSize = 150.dp)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CinevexColors.BackgroundOled)
    ) {
        when {
            isLoading && seriesList.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = CinevexColors.AccentRed)
                }
            }
            error != null && seriesList.isEmpty() -> {
                ErrorStateView(
                    error = error,
                    onRetry = { viewModel.retry() }
                )
            }
            else -> {
                LazyVerticalGrid(
                    columns = gridColumns,
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = if (isTv) 24.dp else 90.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // 1. Featured Hero Carousel
                    if (featuredSeries.isNotEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            FeaturedHeroCarousel(
                                featuredItems = featuredSeries,
                                onPlayClick = { movie ->
                                    navController.navigate(AppScreens.SingleSeries.createRoute(movie.id))
                                },
                                onDetailsClick = { movie ->
                                    navController.navigate(AppScreens.SingleSeries.createRoute(movie.id))
                                },
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                    }

                    // 2. Genre Chips & Filter Row
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            GlassBox(
                                shape = RoundedCornerShape(12.dp),
                                backgroundColor = CinevexColors.GlassSurfaceElevated,
                                onClick = { showFilterSheet = true },
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.FilterList,
                                        contentDescription = "فیلتر",
                                        tint = if (viewModel.advancedFilter.minImdb > 0f) CinevexColors.AccentRed else Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                item {
                                    val isAllSelected = selectedGenreId == 0
                                    GlassBox(
                                        shape = RoundedCornerShape(12.dp),
                                        backgroundColor = if (isAllSelected) CinevexColors.AccentRed else CinevexColors.GlassSurface,
                                        borderColor = if (isAllSelected) CinevexColors.AccentRed else CinevexColors.GlassBorder,
                                        onClick = { viewModel.selectGenre(0) },
                                        enableDpadFocus = isTv
                                    ) {
                                        Text(
                                            text = "همه",
                                            style = CinevexTypography.LabelSmall,
                                            color = if (isAllSelected) Color.White else CinevexColors.TextSecondary,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                                        )
                                    }
                                }

                                items(genres) { genre ->
                                    val isSelected = selectedGenreId == genre.id
                                    GlassBox(
                                        shape = RoundedCornerShape(12.dp),
                                        backgroundColor = if (isSelected) CinevexColors.AccentRed else CinevexColors.GlassSurface,
                                        borderColor = if (isSelected) CinevexColors.AccentRed else CinevexColors.GlassBorder,
                                        onClick = { viewModel.selectGenre(genre.id) },
                                        enableDpadFocus = isTv
                                    ) {
                                        Text(
                                            text = genre.title,
                                            style = CinevexTypography.LabelSmall,
                                            color = if (isSelected) Color.White else CinevexColors.TextSecondary,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 3. Grid of Series Posters
                    itemsIndexed(seriesList) { index, series ->
                        if (index >= seriesList.size - 4 && !isLoadingMore && viewModel.canLoadMore) {
                            LaunchedEffect(Unit) {
                                viewModel.loadMore()
                            }
                        }

                        MediaCard(
                            movie = series.toMovie(),
                            onClick = {
                                navController.navigate(AppScreens.SingleSeries.createRoute(series.id))
                            },
                            onLongClick = {
                                previewSeries = series
                            }
                        )
                    }

                    // 4. Loading More
                    if (isLoadingMore) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = CinevexColors.AccentRed,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Preview BottomSheet
        if (previewSeries != null) {
            MediaPreviewModal(
                movie = previewSeries?.toMovie(),
                onDismiss = { previewSeries = null },
                onPlayClick = { movie ->
                    navController.navigate(AppScreens.SingleSeries.createRoute(movie.id))
                },
                onDetailsClick = { movie ->
                    navController.navigate(AppScreens.SingleSeries.createRoute(movie.id))
                },
                onToggleFavorite = { _ ->
                    previewSeries?.let { series ->
                        val favItem = series.toFavoriteItem()
                        if (StorageUtils.isFavorite(context, series.id, "series")) {
                            StorageUtils.removeFavorite(context, series.id, "series")
                        } else {
                            StorageUtils.saveFavorite(context, favItem)
                        }
                    }
                },
                isFavorite = StorageUtils.isFavorite(context, previewSeries?.id ?: 0, "series")
            )
        }

        // Advanced Filter
        if (showFilterSheet) {
            AdvancedFilterSheet(
                initialFilter = viewModel.advancedFilter,
                onApply = { newFilter -> viewModel.applyAdvancedFilter(newFilter) },
                onDismiss = { showFilterSheet = false }
            )
        }
    }
}
