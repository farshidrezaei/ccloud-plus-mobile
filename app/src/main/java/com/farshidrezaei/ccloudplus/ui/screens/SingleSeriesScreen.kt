package com.farshidrezaei.ccloudplus.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.farshidrezaei.ccloudplus.R
import com.farshidrezaei.ccloudplus.core.theme.CCloudColors
import com.farshidrezaei.ccloudplus.core.theme.CCloudTheme
import com.farshidrezaei.ccloudplus.core.theme.CCloudTypography
import com.farshidrezaei.ccloudplus.core.utils.DownloadUtils
import com.farshidrezaei.ccloudplus.core.utils.StorageUtils
import com.farshidrezaei.ccloudplus.data.model.Episode
import com.farshidrezaei.ccloudplus.data.model.Season
import com.farshidrezaei.ccloudplus.data.model.Series
import com.farshidrezaei.ccloudplus.data.model.Source
import com.farshidrezaei.ccloudplus.data.model.toFavoriteItem
import com.farshidrezaei.ccloudplus.player.VideoPlayerActivity
import com.farshidrezaei.ccloudplus.ui.viewmodel.SeasonsViewModel

private enum class SeriesDetailTab(val title: String) {
    EPISODES("فصل‌ها و قسمت‌ها"),
    ABOUT("درباره سریال"),
    SPECS("مشخصات")
}

@Composable
fun SingleSeriesScreen(
    seriesId: Int,
    navController: NavController
) {
    val context = LocalContext.current
    var series by remember { mutableStateOf<Series?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val seasonsViewModel: SeasonsViewModel = viewModel()

    LaunchedEffect(seriesId) {
        series = StorageUtils.loadSeriesFromFile(context, seriesId)
        seasonsViewModel.loadSeasons(seriesId)
        isLoading = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CCloudTheme.colors.background)
    ) {
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = CCloudColors.AccentRed)
                }
            }
            series != null -> {
                SeriesDetailContent(
                    series = series!!,
                    seasons = seasonsViewModel.seasons,
                    isSeasonsLoading = seasonsViewModel.isLoading,
                    onBackClick = { navController.popBackStack() },
                    onPlayEpisode = { episode, source ->
                        val intent = Intent(context, VideoPlayerActivity::class.java).apply {
                            putExtra("video_url", source.url)
                            putExtra("video_title", "${series!!.title} - ${episode.title} (${source.quality})")
                            putExtra("media_id", series!!.id)
                            putExtra("media_type", "series")
                            putExtra("media_poster", episode.image.ifBlank { series!!.image })
                        }
                        context.startActivity(intent)
                    }
                )
            }
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "سریال مورد نظر یافت نشد.",
                        style = CCloudTypography.TitleMedium,
                        color = CCloudTheme.colors.textSecondary
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SeriesDetailContent(
    series: Series,
    seasons: List<Season>,
    isSeasonsLoading: Boolean,
    onBackClick: () -> Unit,
    onPlayEpisode: (Episode, Source) -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var isFavorite by remember {
        mutableStateOf(StorageUtils.isFavorite(context, series.id, "series"))
    }
    var selectedTab by remember { mutableStateOf(SeriesDetailTab.EPISODES) }
    var selectedSeasonIndex by remember { mutableStateOf(0) }
    var seasonDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // Hero Backdrop with Vignette Gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
        ) {
            val heroImage = series.cover.ifBlank { series.image }
            AsyncImage(
                model = heroImage,
                contentDescription = series.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Vignette overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.5f),
                                Color.Transparent,
                                CCloudTheme.colors.background.copy(alpha = 0.8f),
                                CCloudTheme.colors.background
                            )
                        )
                    )
            )

            // Top bar overlay: Back, Logo, Bookmark
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.45f))
                        .border(1.dp, CCloudColors.GlassBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "بازگشت",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // App Brand in Top Bar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.app_logo),
                        contentDescription = "CCloud+",
                        modifier = Modifier.size(26.dp)
                    )
                    Text(
                        text = "CCLOUD+",
                        style = CCloudTypography.BadgeText.copy(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        ),
                        color = Color.White
                    )
                }

                // Bookmark Button
                IconButton(
                    onClick = {
                        if (isFavorite) {
                            StorageUtils.removeFavorite(context, series.id, "series")
                            isFavorite = false
                            Toast.makeText(context, "از نشان‌شده‌ها حذف شد", Toast.LENGTH_SHORT).show()
                        } else {
                            StorageUtils.saveFavorite(context, series.toFavoriteItem())
                            isFavorite = true
                            Toast.makeText(context, "به نشان‌شده‌ها اضافه شد", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.45f))
                        .border(1.dp, CCloudColors.GlassBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "نشان‌کردن",
                        tint = if (isFavorite) CCloudColors.AccentRed else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Series Poster & Badges overlay at bottom of Hero
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Floating poster
                Box(
                    modifier = Modifier
                        .width(96.dp)
                        .height(140.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, CCloudColors.GlassBorder, RoundedCornerShape(12.dp))
                        .background(CCloudColors.SurfaceDark)
                ) {
                    AsyncImage(
                        model = series.image,
                        contentDescription = series.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Title and Meta
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = series.title,
                        style = CCloudTypography.TitleLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                        color = CCloudTheme.colors.textPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // IMDb Badge
                        if (series.imdb > 0.0) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CCloudColors.RatingGold.copy(alpha = 0.2f))
                                    .border(0.8.dp, CCloudColors.RatingGold.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = CCloudColors.RatingGold,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = String.format("%.1f", series.imdb),
                                    style = CCloudTypography.BadgeText.copy(fontWeight = FontWeight.Bold),
                                    color = CCloudColors.RatingGold
                                )
                            }
                        }

                        if (series.year > 0) {
                            Text(
                                text = "${series.year}",
                                style = CCloudTypography.LabelSmall,
                                color = CCloudTheme.colors.textSecondary
                            )
                        }

                        series.duration?.let {
                            if (it.isNotBlank()) {
                                Text(
                                    text = "• $it",
                                    style = CCloudTypography.LabelSmall,
                                    color = CCloudTheme.colors.textSecondary
                                )
                            }
                        }
                    }

                    // Genres Row
                    if (series.genres.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            items(series.genres) { genre ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(CCloudTheme.colors.surfaceCard)
                                        .border(0.8.dp, CCloudColors.GlassBorder, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 7.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = genre.title,
                                        style = CCloudTypography.LabelSmall.copy(fontSize = 11.sp),
                                        color = CCloudTheme.colors.textSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Primary Action Button (Play Season 1 / First Episode)
        val firstEpisode = seasons.firstOrNull()?.episodes?.firstOrNull()
        val firstSource = firstEpisode?.sources?.firstOrNull()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Button(
                onClick = {
                    if (firstEpisode != null && firstSource != null) {
                        onPlayEpisode(firstEpisode, firstSource)
                    } else {
                        Toast.makeText(context, "در حال بارگیری فصل‌ها...", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CCloudColors.AccentRed,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (seasons.isNotEmpty()) "تماشای ${seasons.first().title}" else "تماشای سریال",
                    style = CCloudTypography.ButtonText.copy(fontSize = 15.sp)
                )
            }
        }

        // Tabs Header (Episodes, About, Specs)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(CCloudTheme.colors.surfaceElevated)
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SeriesDetailTab.entries.forEach { tab ->
                val isSelected = selectedTab == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) CCloudColors.AccentRed else Color.Transparent)
                        .clickable { selectedTab = tab }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab.title,
                        style = CCloudTypography.LabelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = if (isSelected) Color.White else CCloudTheme.colors.textSecondary
                    )
                }
            }
        }

        // Tab Content
        when (selectedTab) {
            SeriesDetailTab.EPISODES -> {
                EpisodesTabContent(
                    seasons = seasons,
                    isLoading = isSeasonsLoading,
                    selectedSeasonIndex = selectedSeasonIndex,
                    seasonDropdownExpanded = seasonDropdownExpanded,
                    onDropdownExpandChange = { seasonDropdownExpanded = it },
                    onSeasonSelect = {
                        selectedSeasonIndex = it
                        seasonDropdownExpanded = false
                    },
                    onPlayEpisode = onPlayEpisode
                )
            }
            SeriesDetailTab.ABOUT -> {
                AboutTabContent(series = series)
            }
            SeriesDetailTab.SPECS -> {
                SpecsTabContent(series = series)
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun EpisodesTabContent(
    seasons: List<Season>,
    isLoading: Boolean,
    selectedSeasonIndex: Int,
    seasonDropdownExpanded: Boolean,
    onDropdownExpandChange: (Boolean) -> Unit,
    onSeasonSelect: (Int) -> Unit,
    onPlayEpisode: (Episode, Source) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = CCloudColors.AccentRed)
            }
            return
        }

        if (seasons.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "قسمتی برای این سریال ثبت نشده است.",
                    style = CCloudTypography.BodyMedium,
                    color = CCloudTheme.colors.textMuted
                )
            }
            return
        }

        val currentSeason = seasons.getOrNull(selectedSeasonIndex) ?: seasons.first()

        // Season Selector Dropdown
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(CCloudTheme.colors.surfaceCard)
                    .border(1.dp, CCloudColors.GlassBorder, RoundedCornerShape(10.dp))
                    .clickable { onDropdownExpandChange(true) }
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentSeason.title,
                    style = CCloudTypography.TitleMedium,
                    color = CCloudTheme.colors.textPrimary
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = CCloudTheme.colors.textSecondary
                )
            }

            DropdownMenu(
                expanded = seasonDropdownExpanded,
                onDismissRequest = { onDropdownExpandChange(false) },
                modifier = Modifier
                    .background(CCloudTheme.colors.surfaceElevated)
                    .border(1.dp, CCloudColors.GlassBorder, RoundedCornerShape(8.dp))
            ) {
                seasons.forEachIndexed { index, season ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = season.title,
                                style = CCloudTypography.BodyMedium,
                                color = if (index == selectedSeasonIndex) CCloudColors.AccentRed else CCloudTheme.colors.textPrimary
                            )
                        },
                        onClick = { onSeasonSelect(index) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Episodes List
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            currentSeason.episodes.forEach { episode ->
                EpisodeGlassCard(
                    episode = episode,
                    onPlay = { source -> onPlayEpisode(episode, source) }
                )
            }
        }
    }
}

@Composable
private fun EpisodeGlassCard(
    episode: Episode,
    onPlay: (Source) -> Unit
) {
    val context = LocalContext.current
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CCloudTheme.colors.surfaceCard)
            .border(1.dp, CCloudColors.GlassBorder, RoundedCornerShape(12.dp))
            .animateContentSize()
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Episode Thumbnail
            Box(
                modifier = Modifier
                    .width(72.dp)
                    .height(52.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CCloudColors.SurfaceDark)
            ) {
                if (episode.image.isNotBlank()) {
                    AsyncImage(
                        model = episode.image,
                        contentDescription = episode.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = null,
                            tint = CCloudTheme.colors.textMuted,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Title & Duration
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = episode.title,
                    style = CCloudTypography.TitleSmall.copy(fontWeight = FontWeight.Bold),
                    color = CCloudTheme.colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                episode.duration?.let { dur ->
                    if (dur.isNotBlank()) {
                        Text(
                            text = dur,
                            style = CCloudTypography.LabelSmall,
                            color = CCloudTheme.colors.textMuted
                        )
                    }
                }
            }

            // Quick Play Button (Plays first source)
            val firstSource = episode.sources.firstOrNull()
            if (firstSource != null) {
                IconButton(
                    onClick = { onPlay(firstSource) },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(CCloudColors.AccentRed)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "پخش",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Expand qualities button
            if (episode.sources.size > 1 || episode.description.isNotBlank()) {
                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "کیفیت‌ها",
                        tint = CCloudTheme.colors.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Expanded Sources & Actions
        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (episode.description.isNotBlank()) {
                    Text(
                        text = episode.description,
                        style = CCloudTypography.BodySmall,
                        color = CCloudTheme.colors.textSecondary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                Text(
                    text = "کیفیت‌های موجود:",
                    style = CCloudTypography.LabelSmall.copy(fontWeight = FontWeight.Bold),
                    color = CCloudTheme.colors.textMuted
                )

                episode.sources.forEach { source ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CCloudTheme.colors.surfaceElevated)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = source.quality,
                            style = CCloudTypography.LabelSmall.copy(fontWeight = FontWeight.Bold),
                            color = CCloudTheme.colors.textPrimary
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            // External VLC Play
                            IconButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        setDataAndType(Uri.parse(source.url), "video/*")
                                        setPackage("org.videolan.vlc")
                                    }
                                    try {
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        val genericIntent = Intent(Intent.ACTION_VIEW).apply {
                                            setDataAndType(Uri.parse(source.url), "video/*")
                                        }
                                        context.startActivity(Intent.createChooser(genericIntent, "پخش با پلیر خارجی"))
                                    }
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.OpenInNew,
                                    contentDescription = "پلیر خارجی",
                                    tint = CCloudTheme.colors.textSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            // Download
                            IconButton(
                                onClick = {
                                    DownloadUtils.openUrl(context, source.url)
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = "دانلود",
                                    tint = CCloudTheme.colors.textSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            // Play
                            Button(
                                onClick = { onPlay(source) },
                                modifier = Modifier.height(28.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CCloudColors.AccentRed,
                                    contentColor = Color.White
                                )
                            ) {
                                Text("پخش", style = CCloudTypography.BadgeText)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AboutTabContent(series: Series) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "خلاصه داستان",
            style = CCloudTypography.TitleMedium,
            color = CCloudTheme.colors.textPrimary
        )

        val synopsis = series.description.ifBlank { "خلاصه‌ای برای این سریال ثبت نشده است." }
        Text(
            text = synopsis,
            style = CCloudTypography.BodyMedium,
            color = CCloudTheme.colors.textSecondary,
            lineHeight = 22.sp
        )
    }
}

@Composable
private fun SpecsTabContent(series: Series) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SpecRow(label = "نوع محتوا", value = "سریال تلویزیونی")
        if (series.year > 0) {
            SpecRow(label = "سال تولید", value = "${series.year}")
        }
        if (series.imdb > 0.0) {
            SpecRow(label = "امتیاز IMDb", value = String.format("%.1f / 10", series.imdb))
        }
        series.duration?.let {
            if (it.isNotBlank()) SpecRow(label = "مدت هر قسمت", value = it)
        }
        if (series.country.isNotEmpty()) {
            SpecRow(label = "کشور سازنده", value = series.country.joinToString("، ") { it.title })
        }
        if (series.genres.isNotEmpty()) {
            SpecRow(label = "ژانرها", value = series.genres.joinToString("، ") { it.title })
        }
    }
}

@Composable
private fun SpecRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CCloudTheme.colors.surfaceCard)
            .border(1.dp, CCloudColors.GlassBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = CCloudTypography.BodySmall,
            color = CCloudTheme.colors.textMuted
        )
        Text(
            text = value,
            style = CCloudTypography.BodySmall.copy(fontWeight = FontWeight.Bold),
            color = CCloudTheme.colors.textPrimary
        )
    }
}
