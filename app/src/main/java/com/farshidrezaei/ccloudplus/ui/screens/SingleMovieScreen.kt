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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.farshidrezaei.ccloudplus.R
import com.farshidrezaei.ccloudplus.core.theme.CCloudColors
import com.farshidrezaei.ccloudplus.core.theme.CCloudTheme
import com.farshidrezaei.ccloudplus.core.theme.CCloudTypography
import com.farshidrezaei.ccloudplus.core.utils.DownloadUtils
import com.farshidrezaei.ccloudplus.core.utils.StorageUtils
import com.farshidrezaei.ccloudplus.data.model.Movie
import com.farshidrezaei.ccloudplus.data.model.Source
import com.farshidrezaei.ccloudplus.data.model.toFavoriteItem
import com.farshidrezaei.ccloudplus.player.VideoPlayerActivity

private enum class MovieDetailTab(val title: String) {
    ABOUT("درباره فیلم"),
    QUALITIES("کیفیت‌های پخش"),
    SPECS("مشخصات")
}

@Composable
fun SingleMovieScreen(
    movieId: Int,
    navController: NavController
) {
    val context = LocalContext.current
    var movie by remember { mutableStateOf<Movie?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(movieId) {
        movie = StorageUtils.loadMovieFromFile(context, movieId)
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
            movie != null -> {
                MovieDetailContent(
                    movie = movie!!,
                    onBackClick = { navController.popBackStack() },
                    onPlaySource = { source ->
                        val intent = Intent(context, VideoPlayerActivity::class.java).apply {
                            putExtra("video_url", source.url)
                            putExtra("video_title", "${movie!!.title} (${source.quality})")
                            putExtra("media_id", movie!!.id)
                            putExtra("media_type", "movie")
                            putExtra("media_poster", movie!!.image)
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
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "اطلاعات فیلم یافت نشد",
                            style = CCloudTypography.TitleLarge,
                            color = CCloudTheme.colors.textPrimary
                        )
                        Button(
                            onClick = { navController.popBackStack() },
                            colors = ButtonDefaults.buttonColors(containerColor = CCloudColors.AccentRed),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("بازگشت", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MovieDetailContent(
    movie: Movie,
    onBackClick: () -> Unit,
    onPlaySource: (Source) -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(MovieDetailTab.ABOUT) }
    var isDescriptionExpanded by remember { mutableStateOf(false) }
    var isFavorite by remember { mutableStateOf(StorageUtils.isFavorite(context, movie.id, "movie")) }

    val defaultSource = remember(movie.sources) {
        movie.sources.firstOrNull { it.url.isNotBlank() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // 1. Cinematic Hero Header with Backdrop
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(380.dp)
        ) {
            // Backdrop Image
            AsyncImage(
                model = if (movie.cover.isNotBlank()) movie.cover else movie.image,
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Deep Vignette Gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0x99000000),
                                Color.Transparent,
                                CCloudTheme.colors.background.copy(alpha = 0.85f),
                                CCloudTheme.colors.background
                            )
                        )
                    )
            )

            // Top Bar Icons: Back + CCloud+ Logo + Favorite
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0x66000000))
                        .border(1.dp, Color(0x33FFFFFF), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "بازگشت",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Official CCloud+ App Logo in Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.app_logo),
                        contentDescription = "CCloud+",
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                    )
                    Text(
                        text = "CCLOUD+",
                        style = CCloudTypography.TitleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        ),
                        color = Color.White
                    )
                }

                // Favorite Toggle Button
                IconButton(
                    onClick = {
                        if (isFavorite) {
                            StorageUtils.removeFavorite(context, movie.id, "movie")
                            isFavorite = false
                            Toast.makeText(context, "از نشان‌شده‌ها حذف شد", Toast.LENGTH_SHORT).show()
                        } else {
                            StorageUtils.saveFavorite(context, movie.toFavoriteItem())
                            isFavorite = true
                            Toast.makeText(context, "به نشان‌شده‌ها افزوده شد", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0x66000000))
                        .border(1.dp, Color(0x33FFFFFF), CircleShape)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "نشان کردن",
                        tint = if (isFavorite) CCloudColors.AccentRed else Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Bottom Hero Content: Poster + Title + Info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .align(Alignment.BottomStart),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                // Movie Poster Thumbnail
                Box(
                    modifier = Modifier
                        .width(105.dp)
                        .height(150.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(12.dp))
                ) {
                    AsyncImage(
                        model = movie.image,
                        contentDescription = movie.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Info Column
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = movie.title,
                        style = CCloudTypography.TitleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Country & Year
                    val countryYearText = buildString {
                        if (movie.country.isNotEmpty()) {
                            append(movie.country.joinToString("، ") { it.title })
                            append(" • ")
                        }
                        if (movie.year > 0) {
                            append(movie.year)
                        }
                    }
                    if (countryYearText.isNotBlank()) {
                        Text(
                            text = countryYearText,
                            style = CCloudTypography.BodySmall,
                            color = CCloudColors.TextSecondary
                        )
                    }

                    // Rating badge
                    if (movie.imdb > 0.0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "امتیاز",
                                tint = CCloudColors.RatingGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = String.format("%.1f", movie.imdb),
                                style = CCloudTypography.TitleSmall.copy(fontWeight = FontWeight.Bold),
                                color = CCloudColors.RatingGold
                            )
                            Text(
                                text = "IMDb",
                                style = CCloudTypography.LabelSmall,
                                color = CCloudColors.TextMuted
                            )
                        }
                    }

                    // Genres Pills
                    if (movie.genres.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            items(movie.genres) { genre ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0x33FFFFFF))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = genre.title,
                                        style = CCloudTypography.LabelSmall,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Big Primary Action Button: "تماشای فیلم"
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {
                    if (defaultSource != null) {
                        onPlaySource(defaultSource)
                    } else {
                        Toast.makeText(context, "لینک پخشی برای این فیلم یافت نشد", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CCloudColors.AccentRed
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "پخش",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "تماشای فیلم",
                        style = CCloudTypography.ButtonText.copy(fontSize = 16.sp),
                        color = Color.White
                    )
                }
            }

            // Secondary Quick Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Play in External Player (VLC / MX Player)
                DetailActionPill(
                    icon = Icons.Default.OpenInNew,
                    label = "پلیر خارجی",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val url = defaultSource?.url ?: ""
                        if (url.isNotBlank()) {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    setDataAndType(Uri.parse(url), "video/*")
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "پلیر خارجی یافت نشد", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "لینک ویدیو موجود نیست", Toast.LENGTH_SHORT).show()
                        }
                    }
                )

                // Direct Download with browser / ADM
                DetailActionPill(
                    icon = Icons.Default.Download,
                    label = "دانلود",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val url = defaultSource?.url ?: ""
                        if (url.isNotBlank()) {
                            DownloadUtils.openUrl(context, url)
                        } else {
                            Toast.makeText(context, "لینک دانلود موجود نیست", Toast.LENGTH_SHORT).show()
                        }
                    }
                )

                // Copy Stream URL
                DetailActionPill(
                    icon = Icons.Default.ContentCopy,
                    label = "کپی لینک",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val url = defaultSource?.url ?: ""
                        if (url.isNotBlank()) {
                            DownloadUtils.copyToClipboard(context, url)
                        } else {
                            Toast.makeText(context, "لینکی برای کپی وجود ندارد", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 3. Tab Navigation ("درباره فیلم" / "کیفیت‌های پخش" / "مشخصات")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MovieDetailTab.entries.forEach { tab ->
                val isSelected = selectedTab == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) CCloudColors.AccentRed else CCloudTheme.colors.surfaceCard
                        )
                        .border(
                            width = 1.dp,
                            color = if (isSelected) CCloudColors.AccentRed else CCloudTheme.colors.glassBorder,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedTab = tab }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab.title,
                        style = CCloudTypography.BodySmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = if (isSelected) Color.White else CCloudTheme.colors.textSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Tab Content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 36.dp)
        ) {
            when (selectedTab) {
                MovieDetailTab.ABOUT -> {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (movie.description.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(CCloudTheme.colors.surfaceCard)
                                    .border(1.dp, CCloudTheme.colors.glassBorder, RoundedCornerShape(14.dp))
                                    .padding(16.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "خلاصه داستان",
                                        style = CCloudTypography.TitleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = CCloudTheme.colors.textPrimary
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = movie.description,
                                        style = CCloudTypography.BodyMedium,
                                        color = CCloudTheme.colors.textSecondary,
                                        maxLines = if (isDescriptionExpanded) Int.MAX_VALUE else 3,
                                        overflow = TextOverflow.Ellipsis,
                                        lineHeight = 22.sp,
                                        modifier = Modifier.animateContentSize()
                                    )
                                    if (movie.description.length > 120) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = if (isDescriptionExpanded) "بستن متن" else "نمایش بیشتر...",
                                            style = CCloudTypography.LabelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = CCloudColors.AccentRed,
                                            modifier = Modifier.clickable {
                                                isDescriptionExpanded = !isDescriptionExpanded
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                MovieDetailTab.QUALITIES -> {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (movie.sources.isEmpty()) {
                            Text(
                                text = "هیچ کیفیتی برای این فیلم ثبت نشده است.",
                                style = CCloudTypography.BodyMedium,
                                color = CCloudTheme.colors.textMuted
                            )
                        } else {
                            movie.sources.forEach { source ->
                                QualityItemCard(
                                    source = source,
                                    onPlay = { onPlaySource(source) },
                                    onDownload = {
                                        DownloadUtils.openUrl(context, source.url)
                                    }
                                )
                            }
                        }
                    }
                }

                MovieDetailTab.SPECS -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SpecRowCard(title = "مدت زمان", value = if (movie.duration.isNullOrBlank()) "نامشخص" else movie.duration!!)
                        SpecRowCard(title = "سال تولید", value = if (movie.year > 0) movie.year.toString() else "نامشخص")
                        SpecRowCard(title = "امتیاز IMDb", value = if (movie.imdb > 0) String.format("%.1f از ۱۰", movie.imdb) else "ثبت نشده")
                        if (movie.country.isNotEmpty()) {
                            SpecRowCard(title = "کشور سازنده", value = movie.country.joinToString("، ") { it.title })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailActionPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(CCloudTheme.colors.surfaceCard)
            .border(1.dp, CCloudTheme.colors.glassBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = CCloudTheme.colors.textPrimary,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = label,
                style = CCloudTypography.LabelSmall,
                color = CCloudTheme.colors.textPrimary
            )
        }
    }
}

@Composable
private fun QualityItemCard(
    source: Source,
    onPlay: () -> Unit,
    onDownload: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CCloudTheme.colors.surfaceCard)
            .border(1.dp, CCloudTheme.colors.glassBorder, RoundedCornerShape(14.dp))
            .clickable { onPlay() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Red Play Button
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(CCloudColors.AccentRed),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "پخش",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    text = source.quality.ifBlank { "کیفیت استاندارد" },
                    style = CCloudTypography.BodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = CCloudTheme.colors.textPrimary
                )
                Text(
                    text = "لینک استریم پرسرعت مستقیم",
                    style = CCloudTypography.LabelSmall,
                    color = CCloudTheme.colors.textMuted
                )
            }
        }

        // Direct Download Icon Button
        IconButton(
            onClick = onDownload,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0x1AFFFFFF))
        ) {
            Icon(
                imageVector = Icons.Default.Download,
                contentDescription = "دانلود",
                tint = CCloudTheme.colors.textPrimary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun SpecRowCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CCloudTheme.colors.surfaceCard)
            .border(1.dp, CCloudTheme.colors.glassBorder, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = CCloudTypography.BodyMedium,
            color = CCloudTheme.colors.textSecondary
        )
        Text(
            text = value,
            style = CCloudTypography.BodyMedium.copy(fontWeight = FontWeight.Bold),
            color = CCloudTheme.colors.textPrimary
        )
    }
}
