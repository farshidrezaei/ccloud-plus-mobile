package com.farshidrezaei.ccloudplus.ui.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.farshidrezaei.ccloudplus.core.config.AppConstants
import com.farshidrezaei.ccloudplus.core.theme.CinevexColors
import com.farshidrezaei.ccloudplus.core.theme.CinevexTypography
import com.farshidrezaei.ccloudplus.data.model.Movie
import kotlinx.coroutines.delay

@Composable
fun FeaturedHeroCarousel(
    featuredItems: List<Movie>,
    onPlayClick: (Movie) -> Unit,
    onDetailsClick: (Movie) -> Unit,
    modifier: Modifier = Modifier
) {
    if (featuredItems.isEmpty()) return

    val items = remember(featuredItems) { featuredItems.take(6) }
    val pagerState = rememberPagerState(pageCount = { items.size })

    // Auto-advance carousel every 7 seconds if not focused
    LaunchedEffect(pagerState) {
        while (true) {
            delay(AppConstants.HERO_CAROUSEL_AUTO_SCROLL_SECONDS * 1000)
            val nextPage = (pagerState.currentPage + 1) % items.size
            pagerState.animateScrollToPage(nextPage, animationSpec = tween(600))
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(340.dp)
            .clip(RoundedCornerShape(20.dp))
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val movie = items[page]
            val interactionSource = remember { MutableInteractionSource() }
            val isFocused by interactionSource.collectIsFocusedAsState()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = { onDetailsClick(movie) }
                    )
                    .focusable(interactionSource = interactionSource)
            ) {
                // Background Backdrop Image
                AsyncImage(
                    model = if (movie.cover.isNotBlank()) movie.cover else movie.image,
                    contentDescription = movie.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Vignette Gradients (Both Vertical and Horizontal for cinema look)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(CinevexColors.HeroVignette)
                )

                // Content Overlay
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    // Rating & Year Badges
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // IMDb Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xE6F59E0B))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = String.format("%.1f", movie.imdb),
                                    style = CinevexTypography.BadgeText,
                                    color = Color.Black
                                )
                            }
                        }

                        // Year Badge
                        Text(
                            text = "${movie.year}",
                            style = CinevexTypography.LabelSmall,
                            color = CinevexColors.TextSecondary
                        )

                        // Genres summary
                        if (movie.genres.isNotEmpty()) {
                            Text(
                                text = "•  " + movie.genres.take(2).joinToString(", ") { it.title },
                                style = CinevexTypography.LabelSmall,
                                color = CinevexColors.TextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Title
                    Text(
                        text = movie.title,
                        style = CinevexTypography.HeroTitle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Description / Synopsis
                    if (movie.description.isNotBlank()) {
                        Text(
                            text = movie.description,
                            style = CinevexTypography.BodyMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Action buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        GlassButton(
                            text = "تماشا",
                            isPrimary = true,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            onClick = { onPlayClick(movie) }
                        )

                        GlassButton(
                            text = "اطلاعات بیشتر",
                            isPrimary = false,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = CinevexColors.TextPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            onClick = { onDetailsClick(movie) }
                        )
                    }
                }
            }
        }

        // Dot indicators at top-right
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(items.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .size(if (isSelected) 8.dp else 6.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) CinevexColors.AccentRed
                            else Color.White.copy(alpha = 0.3f)
                        )
                )
            }
        }
    }
}
