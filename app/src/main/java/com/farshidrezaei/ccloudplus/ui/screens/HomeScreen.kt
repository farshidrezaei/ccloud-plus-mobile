package com.farshidrezaei.ccloudplus.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.farshidrezaei.ccloudplus.core.theme.CCloudColors
import com.farshidrezaei.ccloudplus.core.theme.CCloudTheme
import com.farshidrezaei.ccloudplus.core.theme.CCloudTypography
import com.farshidrezaei.ccloudplus.core.utils.DeviceUtils
import com.farshidrezaei.ccloudplus.core.utils.StorageUtils
import com.farshidrezaei.ccloudplus.data.model.ContinueWatchingItem
import com.farshidrezaei.ccloudplus.data.model.Movie
import com.farshidrezaei.ccloudplus.data.model.Series
import com.farshidrezaei.ccloudplus.player.VideoPlayerActivity
import com.farshidrezaei.ccloudplus.ui.components.ErrorStateView
import com.farshidrezaei.ccloudplus.ui.components.FeaturedHeroCarousel
import com.farshidrezaei.ccloudplus.ui.components.MediaActionSheet
import com.farshidrezaei.ccloudplus.ui.navigation.AppScreens
import com.farshidrezaei.ccloudplus.ui.viewmodel.MoviesViewModel
import com.farshidrezaei.ccloudplus.ui.viewmodel.SeriesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    moviesViewModel: MoviesViewModel = viewModel(),
    seriesViewModel: SeriesViewModel = viewModel()
) {
    val context = LocalContext.current
    val isTv = remember { DeviceUtils.isTv(context) }

    val movies = moviesViewModel.movies
    val featuredMovies = moviesViewModel.featuredItems
    val series = seriesViewModel.seriesList
    val isLoading = moviesViewModel.isLoading && seriesViewModel.isLoading
    val error = moviesViewModel.error ?: seriesViewModel.error

    var continueWatchingList by remember {
        mutableStateOf(StorageUtils.getContinueWatchingList(context))
    }

    LaunchedEffect(Unit) {
        continueWatchingList = StorageUtils.getContinueWatchingList(context)
    }

    // Long press action sheet state
    var selectedMediaForAction by remember {
        mutableStateOf<Pair<Any, String>?>(null) // Pair(Movie/Series, "movie"/"series")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CCloudTheme.colors.background)
    ) {
        when {
            isLoading && movies.isEmpty() && series.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = CCloudColors.AccentRed)
                }
            }
            error != null && movies.isEmpty() && series.isEmpty() -> {
                ErrorStateView(
                    error = error,
                    onRetry = {
                        moviesViewModel.retry()
                        seriesViewModel.retry()
                    }
                )
            }
            else -> {
                PullToRefreshBox(
                    isRefreshing = (moviesViewModel.isLoading || seriesViewModel.isLoading) && (movies.isNotEmpty() || series.isNotEmpty()),
                    onRefresh = {
                        moviesViewModel.refresh()
                        seriesViewModel.refresh()
                        continueWatchingList = StorageUtils.getContinueWatchingList(context)
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            top = 8.dp,
                            bottom = if (isTv) 24.dp else 96.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(22.dp)
                    ) {
                        // 1. Top Bar: Settings Button + CCloud+ Logo + Search Button
                        item {
                            HomeTopBar(
                                onSettingsClick = {
                                    navController.navigate(AppScreens.Settings.route)
                                },
                                onSearchClick = {
                                    navController.navigate(AppScreens.Search.route)
                                }
                            )
                        }

                        // 2. Featured Hero Carousel (Banner)
                        if (featuredMovies.isNotEmpty()) {
                            item {
                                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                    FeaturedHeroCarousel(
                                        featuredItems = featuredMovies,
                                        onPlayClick = { movie ->
                                            val source = movie.sources.firstOrNull()?.url ?: ""
                                            val intent = Intent(context, VideoPlayerActivity::class.java).apply {
                                                putExtra("video_url", source)
                                                putExtra("video_title", movie.title)
                                                putExtra("media_id", movie.id)
                                                putExtra("media_type", "movie")
                                                putExtra("media_poster", movie.image)
                                            }
                                            context.startActivity(intent)
                                        },
                                        onDetailsClick = { movie ->
                                            StorageUtils.saveMovieToFile(context, movie)
                                            navController.navigate(AppScreens.SingleMovie.createRoute(movie.id))
                                        }
                                    )
                                }
                            }
                        }

                        // 3. Continue Watching (ادامه تماشا)
                        if (continueWatchingList.isNotEmpty()) {
                            item {
                                ContinueWatchingSection(
                                    items = continueWatchingList,
                                    onItemClick = { item ->
                                        val intent = Intent(context, VideoPlayerActivity::class.java).apply {
                                            putExtra("video_url", item.videoUrl)
                                            putExtra("video_title", item.title)
                                            putExtra("media_id", item.id)
                                            putExtra("media_type", item.type)
                                            putExtra("media_poster", item.poster)
                                            putExtra("seek_to", item.positionMs)
                                        }
                                        context.startActivity(intent)
                                    },
                                    onRemove = { item ->
                                        StorageUtils.removeContinueWatching(context, item.id, item.type)
                                        continueWatchingList = StorageUtils.getContinueWatchingList(context)
                                    }
                                )
                            }
                        }

                        // 4. Exclusive Recommendations (پیشنهادهای اختصاصی)
                        if (movies.isNotEmpty()) {
                            item {
                                MediaHorizontalRow(
                                    title = "پیشنهادهای اختصاصی",
                                    items = movies.take(10),
                                    onItemClick = { movie ->
                                        StorageUtils.saveMovieToFile(context, movie)
                                        navController.navigate(AppScreens.SingleMovie.createRoute(movie.id))
                                    },
                                    onMoreClick = {
                                        navController.navigate(AppScreens.Movies.route)
                                    }
                                )
                            }
                        }

                        // 5. Latest TV Series (جدیدترین سریال‌ها)
                        if (series.isNotEmpty()) {
                            item {
                                SeriesHorizontalRow(
                                    title = "جدیدترین سریال‌ها",
                                    items = series.take(10),
                                    onItemClick = { s ->
                                        StorageUtils.saveSeriesToFile(context, s)
                                        navController.navigate(AppScreens.SingleSeries.createRoute(s.id))
                                    },
                                    onMoreClick = {
                                        navController.navigate(AppScreens.Series.route)
                                    }
                                )
                            }
                        }

                        // 6. Top Movies of Year (برترین فیلم‌ها)
                        if (movies.size > 10) {
                            item {
                                MediaHorizontalRow(
                                    title = "برترین فیلم‌ها",
                                    items = movies.drop(10).take(10),
                                    onItemClick = { movie ->
                                        StorageUtils.saveMovieToFile(context, movie)
                                        navController.navigate(AppScreens.SingleMovie.createRoute(movie.id))
                                    },
                                    onMoreClick = {
                                        navController.navigate(AppScreens.Movies.route)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Action Bottom Sheet
        selectedMediaForAction?.let { (media, type) ->
            MediaActionSheet(
                item = media,
                mediaType = type,
                onDismiss = { selectedMediaForAction = null }
            )
        }
    }
}

@Composable
private fun HomeTopBar(
    onSettingsClick: () -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Settings Button (Replaces profile/bell as requested)
        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(CCloudColors.GlassSurface)
                .border(1.dp, CCloudColors.GlassBorder, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "تنظیمات",
                tint = CCloudColors.TextPrimary,
                modifier = Modifier.size(22.dp)
            )
        }

        // CCloud+ Signature Logo in Center
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Image(
                painter = painterResource(com.farshidrezaei.ccloudplus.R.drawable.app_logo),
                contentDescription = "CCloud+ Logo",
                modifier = Modifier.size(28.dp)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "CCLOUD",
                    style = CCloudTypography.TitleMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    ),
                    color = CCloudTheme.colors.textPrimary
                )
                Text(
                    text = "+",
                    style = CCloudTypography.TitleMedium.copy(
                        fontWeight = FontWeight.Black
                    ),
                    color = CCloudColors.AccentRed
                )
            }
        }

        // Search Button
        IconButton(
            onClick = onSearchClick,
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(CCloudColors.GlassSurface)
                .border(1.dp, CCloudColors.GlassBorder, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "جستجو",
                tint = CCloudColors.TextPrimary,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun ContinueWatchingSection(
    items: List<ContinueWatchingItem>,
    onItemClick: (ContinueWatchingItem) -> Unit,
    onRemove: (ContinueWatchingItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ادامه تماشا",
                style = CCloudTypography.TitleSmall.copy(fontWeight = FontWeight.Bold),
                color = CCloudColors.TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(items) { item ->
                ContinueWatchingCard(
                    item = item,
                    onClick = { onItemClick(item) }
                )
            }
        }
    }
}

@Composable
private fun ContinueWatchingCard(
    item: ContinueWatchingItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isFocused) 1.05f else 1.0f,
        label = "scale"
    )

    Column(
        modifier = modifier
            .width(180.dp)
            .scale(scale)
            .clip(RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .focusable(interactionSource = interactionSource)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(105.dp)
                .clip(RoundedCornerShape(14.dp))
                .border(
                    width = if (isFocused) 2.dp else 1.dp,
                    color = if (isFocused) CCloudColors.GlassBorderFocus else CCloudColors.GlassBorder,
                    shape = RoundedCornerShape(14.dp)
                )
        ) {
            AsyncImage(
                model = item.poster,
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Dim gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x55000000))
            )

            // Play Icon in Center
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(CCloudColors.AccentRed)
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "پخش",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Progress bar at bottom of card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(Color(0x66000000))
                    .align(Alignment.BottomCenter)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction = item.progressFloat)
                        .background(CCloudColors.AccentRed)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = item.title,
            style = CCloudTypography.BodySmall.copy(fontWeight = FontWeight.Medium),
            color = CCloudColors.TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun MediaHorizontalRow(
    title: String,
    items: List<Movie>,
    onItemClick: (Movie) -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = CCloudTypography.TitleSmall.copy(fontWeight = FontWeight.Bold),
                color = CCloudColors.TextPrimary
            )

            Text(
                text = "مشاهده همه",
                style = CCloudTypography.LabelSmall,
                color = CCloudColors.AccentRed,
                modifier = Modifier.clickable { onMoreClick() }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { movie ->
                MiniPosterCard(
                    title = movie.title,
                    posterUrl = movie.image,
                    rating = movie.imdb,
                    year = movie.year,
                    onClick = { onItemClick(movie) }
                )
            }
        }
    }
}

@Composable
private fun SeriesHorizontalRow(
    title: String,
    items: List<Series>,
    onItemClick: (Series) -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = CCloudTypography.TitleSmall.copy(fontWeight = FontWeight.Bold),
                color = CCloudColors.TextPrimary
            )

            Text(
                text = "مشاهده همه",
                style = CCloudTypography.LabelSmall,
                color = CCloudColors.AccentRed,
                modifier = Modifier.clickable { onMoreClick() }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { s ->
                MiniPosterCard(
                    title = s.title,
                    posterUrl = s.image,
                    rating = s.imdb,
                    year = s.year,
                    onClick = { onItemClick(s) }
                )
            }
        }
    }
}

@Composable
private fun MiniPosterCard(
    title: String,
    posterUrl: String,
    rating: Double,
    year: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isFocused) 1.08f else 1.0f,
        label = "scale"
    )

    Column(
        modifier = modifier
            .width(120.dp)
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .focusable(interactionSource = interactionSource)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(175.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(
                    width = if (isFocused) 2.dp else 1.dp,
                    color = if (isFocused) CCloudColors.GlassBorderFocus else CCloudColors.GlassBorder,
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            AsyncImage(
                model = posterUrl,
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // IMDb rating badge on top right (RTL start)
            if (rating > 0.0) {
                Box(
                    modifier = Modifier
                        .padding(6.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xCC000000))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                        .align(Alignment.TopEnd)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = CCloudColors.RatingGold,
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = String.format("%.1f", rating),
                            style = CCloudTypography.LabelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White
                        )
                    }
                }
            }

            // Year badge on bottom
            if (year > 0) {
                Box(
                    modifier = Modifier
                        .padding(6.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0x99000000))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                        .align(Alignment.BottomStart)
                ) {
                    Text(
                        text = "$year",
                        style = CCloudTypography.LabelSmall.copy(fontSize = 9.sp),
                        color = CCloudColors.TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = title,
            style = CCloudTypography.BodySmall.copy(fontWeight = FontWeight.Medium),
            color = CCloudColors.TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
