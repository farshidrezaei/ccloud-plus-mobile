package com.farshidrezaei.ccloudplus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.farshidrezaei.ccloudplus.core.theme.CinevexColors
import com.farshidrezaei.ccloudplus.core.theme.CinevexTypography
import com.farshidrezaei.ccloudplus.data.model.Episode

@Composable
fun EpisodeItemCard(
    episode: Episode,
    isWatched: Boolean,
    onPlayClick: (Episode) -> Unit,
    modifier: Modifier = Modifier
) {
    GlassBox(
        shape = RoundedCornerShape(14.dp),
        backgroundColor = if (isWatched) CinevexColors.GlassSurface else CinevexColors.GlassSurfaceElevated,
        borderColor = if (isWatched) CinevexColors.SuccessGreen.copy(alpha = 0.4f) else CinevexColors.GlassBorder,
        onClick = { onPlayClick(episode) },
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Thumbnail
            Box(
                modifier = Modifier
                    .width(110.dp)
                    .height(68.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                AsyncImage(
                    model = episode.image,
                    contentDescription = episode.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Play icon overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.35f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Title & Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = episode.title,
                        style = CinevexTypography.TitleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    if (isWatched) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "دیده شده",
                            tint = CinevexColors.SuccessGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                if (episode.description.isNotBlank()) {
                    Text(
                        text = episode.description,
                        style = CinevexTypography.LabelSmall,
                        color = CinevexColors.TextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (!episode.duration.isNullOrBlank()) {
                    Text(
                        text = episode.duration,
                        style = CinevexTypography.LabelSmall,
                        color = CinevexColors.TextMuted
                    )
                }
            }
        }
    }
}
