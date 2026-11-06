package com.farshidrezaei.ccloudplus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.farshidrezaei.ccloudplus.core.theme.CinevexColors
import com.farshidrezaei.ccloudplus.core.theme.CinevexTypography
import com.farshidrezaei.ccloudplus.data.model.Genre

@Composable
fun MediaDetailHeader(
    title: String,
    coverUrl: String,
    imageUrl: String,
    imdb: Double,
    year: Int,
    duration: String?,
    genres: List<Genre>,
    isFavorite: Boolean,
    onPlayClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(380.dp)
    ) {
        // Backdrop Image
        AsyncImage(
            model = if (coverUrl.isNotBlank()) coverUrl else imageUrl,
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient Vignette
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CinevexColors.HeroVignette)
        )

        // Content
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Poster Card
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(110.dp)
                    .height(160.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            // Info & Actions
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // IMDb & Year
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xE6F59E0B))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = String.format("%.1f", imdb),
                                style = CinevexTypography.BadgeText,
                                color = Color.Black
                            )
                        }
                    }

                    Text(
                        text = "$year",
                        style = CinevexTypography.LabelSmall,
                        color = CinevexColors.TextSecondary
                    )

                    if (!duration.isNullOrBlank()) {
                        Text(
                            text = "•  $duration",
                            style = CinevexTypography.LabelSmall,
                            color = CinevexColors.TextMuted
                        )
                    }
                }

                // Title
                Text(
                    text = title,
                    style = CinevexTypography.HeroTitle,
                    maxLines = 2
                )

                // Genres
                if (genres.isNotEmpty()) {
                    Text(
                        text = genres.joinToString(" • ") { it.title },
                        style = CinevexTypography.LabelSmall,
                        color = CinevexColors.TextSecondary,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Actions
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    GlassButton(
                        text = "پخش مستقیم",
                        isPrimary = true,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        onClick = onPlayClick
                    )

                    GlassButton(
                        text = if (isFavorite) "حذف نشان" else "علاقه‌مندی",
                        isPrimary = false,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = if (isFavorite) CinevexColors.AccentRed else Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        onClick = onToggleFavorite
                    )
                }
            }
        }
    }
}
