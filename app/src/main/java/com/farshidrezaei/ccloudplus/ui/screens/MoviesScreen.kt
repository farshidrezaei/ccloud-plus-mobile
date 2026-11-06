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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.farshidrezaei.ccloudplus.core.theme.CCloudColors
import com.farshidrezaei.ccloudplus.core.theme.CCloudTypography
import com.farshidrezaei.ccloudplus.core.utils.DeviceUtils
import com.farshidrezaei.ccloudplus.core.utils.StorageUtils
import com.farshidrezaei.ccloudplus.data.model.Movie
import com.farshidrezaei.ccloudplus.ui.components.*
import com.farshidrezaei.ccloudplus.ui.navigation.AppScreens
import com.farshidrezaei.ccloudplus.ui.viewmodel.MoviesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesScreen(
    navController: NavController,
    viewModel: MoviesViewModel = viewModel()
) {
    val context = LocalContext.current
    val isTv = remember { DeviceUtils.isTv(context) }

    val movies = viewModel.movies
    val isLoading = viewModel.isLoading
    val isLoadingMore = viewModel.isLoadingMore
    val error = viewModel.error
    val genres = viewModel.genres
    val selectedGenreId = viewModel.selectedGenreId

    var selectedMovieForAction by remember { mutableStateOf<Movie?>(null) }
    var showFilterSheet by remember { mutableStateOf(false) }

    // 3 columns on mobile, 6 columns on TV (matches Image 3 benchmark)
    val gridColumns = remember(isTv) {
        if (isTv) GridCells.Fixed(6) else GridCells.Fixed(3)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CCloudColors.BackgroundOled)
    ) {
        when {
            isLoading && movies.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = CCloudColors.AccentRed)
                }
            }
            error != null && movies.isEmpty() -> {
                ErrorStateView(
                    error = error,
                    onRetry = { viewModel.retry() }
                )
            }
            else -> {
                PullToRefreshBox(
                    isRefreshing = isLoading && movies.isNotEmpty(),
                    onRefresh = { viewModel.refresh() },
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyVerticalGrid(
                        columns = gridColumns,
                        contentPadding = PaddingValues(
                            start = 12.dp,
                            end = 12.dp,
                            top = 12.dp,
                            bottom = if (isTv) 24.dp else 96.dp
                        ),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // 1. Header Title
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "فیلم‌ها",
                                    style = CCloudTypography.TitleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = CCloudColors.TextPrimary
                                )
                            }
                        }

                        // 2. Genre Chips & Filter Row (Full span)
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Filter Button
                                GlassBox(
                                    shape = RoundedCornerShape(12.dp),
                                    backgroundColor = CCloudColors.GlassSurfaceElevated,
                                    onClick = { showFilterSheet = true },
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.FilterList,
                                            contentDescription = "فیلتر",
                                            tint = if (viewModel.advancedFilter.minImdb > 0f) CCloudColors.AccentRed else Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Genre list
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    item {
                                        val isAllSelected = selectedGenreId == 0
                                        GlassBox(
                                            shape = RoundedCornerShape(12.dp),
                                            backgroundColor = if (isAllSelected) CCloudColors.AccentRed else CCloudColors.GlassSurface,
                                            borderColor = if (isAllSelected) CCloudColors.AccentRed else CCloudColors.GlassBorder,
                                            onClick = { viewModel.selectGenre(0) },
                                            enableDpadFocus = isTv
                                        ) {
                                            Text(
                                                text = "همه",
                                                style = CCloudTypography.LabelSmall,
                                                color = if (isAllSelected) Color.White else CCloudColors.TextSecondary,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                                            )
                                        }
                                    }

                                    items(genres) { genre ->
                                        val isSelected = selectedGenreId == genre.id
                                        GlassBox(
                                            shape = RoundedCornerShape(12.dp),
                                            backgroundColor = if (isSelected) CCloudColors.AccentRed else CCloudColors.GlassSurface,
                                            borderColor = if (isSelected) CCloudColors.AccentRed else CCloudColors.GlassBorder,
                                            onClick = { viewModel.selectGenre(genre.id) },
                                            enableDpadFocus = isTv
                                        ) {
                                            Text(
                                                text = genre.title,
                                                style = CCloudTypography.LabelSmall,
                                                color = if (isSelected) Color.White else CCloudColors.TextSecondary,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 3. Grid of Movie Posters (3-column on mobile)
                        itemsIndexed(movies) { index, movie ->
                            if (index >= movies.size - 4 && !isLoadingMore && viewModel.canLoadMore) {
                                LaunchedEffect(Unit) {
                                    viewModel.loadMore()
                                }
                            }

                            MediaCard(
                                movie = movie,
                                onClick = {
                                    StorageUtils.saveMovieToFile(context, movie)
                                    navController.navigate(AppScreens.SingleMovie.createRoute(movie.id))
                                },
                                onLongClick = {
                                    selectedMovieForAction = movie
                                }
                            )
                        }

                        // 4. Loading More Spinner
                        if (isLoadingMore) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = CCloudColors.AccentRed,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quick Action Bottom Sheet
        selectedMovieForAction?.let { movie ->
            MediaActionSheet(
                item = movie,
                mediaType = "movie",
                onDismiss = { selectedMovieForAction = null }
            )
        }

        // Advanced Filter Bottom Sheet
        if (showFilterSheet) {
            AdvancedFilterSheet(
                initialFilter = viewModel.advancedFilter,
                onApply = { newFilter ->
                    viewModel.applyAdvancedFilter(newFilter)
                    showFilterSheet = false
                },
                onDismiss = { showFilterSheet = false }
            )
        }
    }
}
