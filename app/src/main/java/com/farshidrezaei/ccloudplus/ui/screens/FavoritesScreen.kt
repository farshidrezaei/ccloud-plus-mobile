package com.farshidrezaei.ccloudplus.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.farshidrezaei.ccloudplus.core.theme.CCloudColors
import com.farshidrezaei.ccloudplus.core.theme.CCloudTypography
import com.farshidrezaei.ccloudplus.core.utils.DeviceUtils
import com.farshidrezaei.ccloudplus.core.utils.StorageUtils
import com.farshidrezaei.ccloudplus.data.model.FavoriteItem
import com.farshidrezaei.ccloudplus.data.model.toMovie
import com.farshidrezaei.ccloudplus.data.model.toSeries
import com.farshidrezaei.ccloudplus.ui.navigation.AppScreens

private enum class FavoriteTab(val title: String) {
    ALL("همه"),
    MOVIES("فیلم‌ها"),
    SERIES("سریال‌ها")
}

@Composable
fun FavoritesScreen(navController: NavController) {
    val context = LocalContext.current
    val isTv = remember { DeviceUtils.isTv(context) }

    var favorites by remember { mutableStateOf(StorageUtils.loadAllFavorites(context)) }
    var selectedTab by remember { mutableStateOf(FavoriteTab.ALL) }

    LaunchedEffect(Unit) {
        favorites = StorageUtils.loadAllFavorites(context)
    }

    val filteredFavorites = remember(favorites, selectedTab) {
        when (selectedTab) {
            FavoriteTab.ALL -> favorites
            FavoriteTab.MOVIES -> favorites.filter { it.type == "movie" }
            FavoriteTab.SERIES -> favorites.filter { it.type == "series" }
        }
    }

    val moviesCount = remember(favorites) { favorites.count { it.type == "movie" } }
    val seriesCount = remember(favorites) { favorites.count { it.type == "series" } }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CCloudColors.BackgroundOled)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 12.dp,
                    bottom = if (isTv) 24.dp else 96.dp
                )
        ) {
            // 1. Header (نشان‌شده‌ها)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "نشان‌شده‌ها",
                    style = CCloudTypography.TitleMedium.copy(fontWeight = FontWeight.Bold),
                    color = CCloudColors.TextPrimary
                )

                // Delete All Button if not empty
                if (favorites.isNotEmpty()) {
                    var showClearConfirm by remember { mutableStateOf(false) }

                    IconButton(
                        onClick = { showClearConfirm = true },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CCloudColors.GlassSurface)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "پاک کردن همه",
                            tint = CCloudColors.TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    if (showClearConfirm) {
                        AlertDialog(
                            onDismissRequest = { showClearConfirm = false },
                            containerColor = CCloudColors.SurfaceDark,
                            title = {
                                Text(
                                    text = "حذف همه نشان‌شده‌ها",
                                    style = CCloudTypography.TitleSmall,
                                    color = CCloudColors.TextPrimary
                                )
                            },
                            text = {
                                Text(
                                    text = "آیا مطمئن هستید که می‌خواهید تمام فیلم‌ها و سریال‌های نشان‌شده را پاک کنید؟",
                                    style = CCloudTypography.BodyMedium,
                                    color = CCloudColors.TextSecondary
                                )
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        StorageUtils.clearAllFavorites(context)
                                        favorites = emptyList()
                                        showClearConfirm = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = CCloudColors.AccentRed)
                                ) {
                                    Text("بله، پاک کن", color = Color.White)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showClearConfirm = false }) {
                                    Text("انصراف", color = CCloudColors.TextSecondary)
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 2. Filter Tabs (همه / فیلم‌ها / سریال‌ها) with counts
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FavoriteTab.entries.forEach { tab ->
                    val isSelected = selectedTab == tab
                    val count = when (tab) {
                        FavoriteTab.ALL -> favorites.size
                        FavoriteTab.MOVIES -> moviesCount
                        FavoriteTab.SERIES -> seriesCount
                    }

                    val tabTitle = "${tab.title} ($count)"

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isSelected) CCloudColors.AccentRed else CCloudColors.GlassSurface
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) CCloudColors.AccentRed else CCloudColors.GlassBorder,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable { selectedTab = tab }
                            .padding(horizontal = 16.dp, vertical = 7.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tabTitle,
                            style = CCloudTypography.LabelSmall.copy(fontWeight = FontWeight.Medium),
                            color = if (isSelected) Color.White else CCloudColors.TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Favorite Items List
            if (filteredFavorites.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = CCloudColors.TextMuted,
                            modifier = Modifier.size(56.dp)
                        )
                        Text(
                            text = "هنوز هیچ فیلم یا سریالی را نشان نکرده‌اید",
                            style = CCloudTypography.BodyMedium,
                            color = CCloudColors.TextSecondary
                        )
                        Button(
                            onClick = {
                                navController.navigate(AppScreens.Movies.route)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CCloudColors.AccentRed),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("مرور فیلم‌ها", color = Color.White)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = filteredFavorites,
                        key = { "${it.type}_${it.id}" }
                    ) { item ->
                        FavoriteListItem(
                            item = item,
                            onClick = {
                                if (item.type == "movie") {
                                    StorageUtils.saveFavoriteToDatabase(context, item)
                                    navController.navigate(AppScreens.SingleMovie.createRoute(item.id))
                                } else {
                                    StorageUtils.saveFavoriteToDatabase(context, item)
                                    navController.navigate(AppScreens.SingleSeries.createRoute(item.id))
                                }
                            },
                            onRemove = {
                                StorageUtils.removeFavorite(context, item.id, item.type)
                                favorites = StorageUtils.loadAllFavorites(context)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteListItem(
    item: FavoriteItem,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isFocused) 1.02f else 1.0f,
        label = "fav_scale"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(14.dp))
            .background(CCloudColors.GlassSurface)
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = if (isFocused) CCloudColors.GlassBorderFocus else CCloudColors.GlassBorder,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .focusable(interactionSource = interactionSource)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Red Heart Remove Button on right (in RTL) / action side
        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0x22E50914))
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "حذف از نشان‌شده‌ها",
                tint = CCloudColors.AccentRed,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Info Column
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = item.title,
                style = CCloudTypography.BodyMedium.copy(fontWeight = FontWeight.Bold),
                color = CCloudColors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Type badge
                Text(
                    text = if (item.type == "movie") "فیلم" else "سریال",
                    style = CCloudTypography.LabelSmall,
                    color = CCloudColors.TextMuted
                )

                if (item.year > 0) {
                    Text(
                        text = "• ${item.year}",
                        style = CCloudTypography.LabelSmall,
                        color = CCloudColors.TextMuted
                    )
                }

                if (item.imdb > 0.0) {
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
                            text = String.format("%.1f", item.imdb),
                            style = CCloudTypography.LabelSmall.copy(fontWeight = FontWeight.Bold),
                            color = CCloudColors.RatingGold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Poster Thumbnail
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(84.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(CCloudColors.SurfaceDark)
        ) {
            AsyncImage(
                model = item.image,
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
