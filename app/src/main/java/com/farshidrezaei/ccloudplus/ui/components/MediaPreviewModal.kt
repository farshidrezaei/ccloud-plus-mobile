package com.farshidrezaei.ccloudplus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
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
import com.farshidrezaei.ccloudplus.data.model.Movie

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaPreviewModal(
    movie: Movie?,
    onDismiss: () -> Unit,
    onPlayClick: (Movie) -> Unit,
    onDetailsClick: (Movie) -> Unit,
    onToggleFavorite: (Movie) -> Unit,
    isFavorite: Boolean = false
) {
    if (movie == null) return

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = CinevexColors.SurfaceDark,
        scrimColor = Color.Black.copy(alpha = 0.75f),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.25f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header Image & Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Poster thumbnail
                AsyncImage(
                    model = movie.image,
                    contentDescription = movie.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(90.dp)
                        .height(130.dp)
                        .clip(RoundedCornerShape(12.dp))
                )

                // Title and Meta
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = movie.title,
                        style = CinevexTypography.TitleLarge
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Rating
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = CinevexColors.RatingGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = String.format("%.1f", movie.imdb),
                                style = CinevexTypography.BadgeText,
                                color = CinevexColors.TextPrimary
                            )
                        }

                        Text(
                            text = "•",
                            style = CinevexTypography.LabelSmall,
                            color = CinevexColors.TextMuted
                        )

                        Text(
                            text = "${movie.year}",
                            style = CinevexTypography.LabelSmall,
                            color = CinevexColors.TextSecondary
                        )

                        if (!movie.duration.isNullOrBlank()) {
                            Text(
                                text = "•",
                                style = CinevexTypography.LabelSmall,
                                color = CinevexColors.TextMuted
                            )
                            Text(
                                text = movie.duration,
                                style = CinevexTypography.LabelSmall,
                                color = CinevexColors.TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Genres
                    Text(
                        text = movie.genres.joinToString("، ") { it.title },
                        style = CinevexTypography.LabelSmall,
                        color = CinevexColors.TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Plot Description
            if (movie.description.isNotBlank()) {
                Text(
                    text = movie.description,
                    style = CinevexTypography.BodyMedium,
                    maxLines = 4
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
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
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onDismiss()
                        onPlayClick(movie)
                    }
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
                    onClick = { onToggleFavorite(movie) }
                )

                GlassButton(
                    text = "جزئیات",
                    isPrimary = false,
                    onClick = {
                        onDismiss()
                        onDetailsClick(movie)
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
