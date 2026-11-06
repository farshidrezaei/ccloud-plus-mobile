package com.farshidrezaei.ccloudplus.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.farshidrezaei.ccloudplus.core.theme.CinevexColors
import com.farshidrezaei.ccloudplus.core.theme.CinevexTypography
import com.farshidrezaei.ccloudplus.data.model.Movie

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaCard(
    movie: Movie,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.08f else 1.0f,
        animationSpec = tween(180),
        label = "card_scale"
    )

    Column(
        modifier = modifier
            .scale(scale)
            .then(
                if (isFocused) {
                    Modifier.shadow(16.dp, RoundedCornerShape(14.dp), spotColor = CinevexColors.AccentRedGlow)
                } else Modifier
            )
            .clip(RoundedCornerShape(14.dp))
            .background(CinevexColors.GlassSurface)
            .border(
                if (isFocused) 2.dp else 1.dp,
                if (isFocused) CinevexColors.GlassBorderFocus else CinevexColors.GlassBorder,
                RoundedCornerShape(14.dp)
            )
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
                onLongClick = onLongClick
            )
            .focusable(interactionSource = interactionSource)
            .padding(6.dp)
    ) {
        // Poster Image Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.68f)
                .clip(RoundedCornerShape(10.dp))
        ) {
            AsyncImage(
                model = movie.image,
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Rating Pill at top-left
            Box(
                modifier = Modifier
                    .padding(6.dp)
                    .align(Alignment.TopStart)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Black.copy(alpha = 0.75f))
                    .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = CinevexColors.RatingGold,
                        modifier = Modifier.size(11.dp)
                    )
                    Text(
                        text = String.format("%.1f", movie.imdb),
                        style = CinevexTypography.BadgeText,
                        color = Color.White
                    )
                }
            }

            // Year Pill at top-right
            if (movie.year > 0) {
                Box(
                    modifier = Modifier
                        .padding(6.dp)
                        .align(Alignment.TopEnd)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.75f))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${movie.year}",
                        style = CinevexTypography.BadgeText,
                        color = CinevexColors.TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Title
        Text(
            text = movie.title,
            style = CinevexTypography.TitleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Genres
        if (movie.genres.isNotEmpty()) {
            Text(
                text = movie.genres.take(2).joinToString(" • ") { it.title },
                style = CinevexTypography.LabelSmall,
                color = CinevexColors.TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
}
