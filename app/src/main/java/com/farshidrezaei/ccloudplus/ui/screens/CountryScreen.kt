package com.farshidrezaei.ccloudplus.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.farshidrezaei.ccloudplus.core.theme.CCloudColors
import com.farshidrezaei.ccloudplus.core.theme.CCloudTheme
import com.farshidrezaei.ccloudplus.core.theme.CCloudTypography
import com.farshidrezaei.ccloudplus.core.utils.DeviceUtils
import com.farshidrezaei.ccloudplus.core.utils.StorageUtils
import com.farshidrezaei.ccloudplus.data.model.FilterType
import com.farshidrezaei.ccloudplus.data.model.Poster
import com.farshidrezaei.ccloudplus.data.model.toMovie
import com.farshidrezaei.ccloudplus.ui.components.MediaActionSheet
import com.farshidrezaei.ccloudplus.ui.components.MediaCard
import com.farshidrezaei.ccloudplus.ui.navigation.AppScreens
import com.farshidrezaei.ccloudplus.ui.viewmodel.CountryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryScreen(
    countryId: Int = 0,
    viewModel: CountryViewModel = viewModel(),
    navController: NavController? = null
) {
    val context = LocalContext.current
    val isTv = remember { DeviceUtils.isTv(context) }

    LaunchedEffect(countryId) {
        viewModel.setCountryId(countryId)
    }

    val posters = viewModel.posters
    val countryName = viewModel.countryName
    val isLoading = viewModel.isLoading
    val isLoadingMore = viewModel.isLoadingMore
    val errorMessage = viewModel.errorMessage
    val selectedFilterType = viewModel.selectedFilterType

    var showFilterMenu by remember { mutableStateOf(false) }
    var selectedPosterForAction by remember { mutableStateOf<Poster?>(null) }

    val gridColumns = remember(isTv) {
        if (isTv) GridCells.Fixed(6) else GridCells.Fixed(3)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CCloudTheme.colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Modern Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button
                IconButton(
                    onClick = { navController?.popBackStack() },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(CCloudTheme.colors.surfaceElevated)
                        .border(1.dp, CCloudColors.GlassBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "بازگشت",
                        tint = CCloudTheme.colors.textPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Country Title
                Text(
                    text = if (countryName.isNotBlank()) countryName else "فیلم‌ها و سریال‌ها",
                    style = CCloudTypography.TitleLarge,
                    color = CCloudTheme.colors.textPrimary
                )

                // Filter button
                Box {
                    IconButton(
                        onClick = { showFilterMenu = true },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(CCloudTheme.colors.surfaceElevated)
                            .border(1.dp, CCloudColors.GlassBorder, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "فیلتر",
                            tint = CCloudTheme.colors.textPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showFilterMenu,
                        onDismissRequest = { showFilterMenu = false },
                        modifier = Modifier
                            .background(CCloudTheme.colors.surfaceElevated)
                            .border(1.dp, CCloudColors.GlassBorder, RoundedCornerShape(8.dp))
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "پیش‌فرض (جدیدترین)",
                                    style = CCloudTypography.BodyMedium,
                                    color = if (selectedFilterType == FilterType.DEFAULT) CCloudColors.AccentRed else CCloudTheme.colors.textPrimary
                                )
                            },
                            onClick = {
                                viewModel.setFilterType(FilterType.DEFAULT)
                                showFilterMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "بر اساس سال انتشار",
                                    style = CCloudTypography.BodyMedium,
                                    color = if (selectedFilterType == FilterType.BY_YEAR) CCloudColors.AccentRed else CCloudTheme.colors.textPrimary
                                )
                            },
                            onClick = {
                                viewModel.setFilterType(FilterType.BY_YEAR)
                                showFilterMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "بر اساس امتیاز IMDb",
                                    style = CCloudTypography.BodyMedium,
                                    color = if (selectedFilterType == FilterType.BY_IMDB) CCloudColors.AccentRed else CCloudTheme.colors.textPrimary
                                )
                            },
                            onClick = {
                                viewModel.setFilterType(FilterType.BY_IMDB)
                                showFilterMenu = false
                            }
                        )
                    }
                }
            }

            // Main Content Area
            when {
                isLoading && posters.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = CCloudColors.AccentRed)
                    }
                }
                errorMessage != null && posters.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "خطا در دریافت اطلاعات",
                                style = CCloudTypography.TitleMedium,
                                color = CCloudTheme.colors.textPrimary
                            )
                            Button(
                                onClick = { viewModel.refresh() },
                                colors = ButtonDefaults.buttonColors(containerColor = CCloudColors.AccentRed),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("تلاش مجدد", style = CCloudTypography.ButtonText)
                            }
                        }
                    }
                }
                else -> {
                    PullToRefreshBox(
                        isRefreshing = isLoading,
                        onRefresh = { viewModel.refresh() },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LazyVerticalGrid(
                            columns = gridColumns,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 96.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            itemsIndexed(
                                items = posters,
                                key = { index, poster -> "${poster.id}_${poster.type}_$index" }
                            ) { index, poster ->
                                if (index >= posters.size - 4 && !isLoadingMore && viewModel.canLoadMore) {
                                    LaunchedEffect(Unit) {
                                        viewModel.loadMore()
                                    }
                                }

                                MediaCard(
                                    movie = poster.toMovie(),
                                    onClick = {
                                        if (poster.type == "serie" || poster.type == "series") {
                                            StorageUtils.saveSeriesToFile(context, poster.toSeries())
                                            navController?.navigate(AppScreens.SingleSeries.createRoute(poster.id))
                                        } else {
                                            StorageUtils.saveMovieToFile(context, poster.toMovie())
                                            navController?.navigate(AppScreens.SingleMovie.createRoute(poster.id))
                                        }
                                    },
                                    onLongClick = {
                                        selectedPosterForAction = poster
                                    }
                                )
                            }

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
                                            modifier = Modifier.size(28.dp),
                                            strokeWidth = 2.5.dp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Action Sheet
        selectedPosterForAction?.let { poster ->
            MediaActionSheet(
                item = poster.toMovie(),
                onDismiss = { selectedPosterForAction = null },
                onNavigateToDetail = {
                    val p = poster
                    selectedPosterForAction = null
                    if (p.type == "serie" || p.type == "series") {
                        StorageUtils.saveSeriesToFile(context, p.toSeries())
                        navController?.navigate(AppScreens.SingleSeries.createRoute(p.id))
                    } else {
                        StorageUtils.saveMovieToFile(context, p.toMovie())
                        navController?.navigate(AppScreens.SingleMovie.createRoute(p.id))
                    }
                }
            )
        }
    }
}
