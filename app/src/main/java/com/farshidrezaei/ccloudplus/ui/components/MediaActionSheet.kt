package com.farshidrezaei.ccloudplus.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.farshidrezaei.ccloudplus.core.theme.CCloudColors
import com.farshidrezaei.ccloudplus.core.theme.CCloudTypography
import com.farshidrezaei.ccloudplus.core.utils.DownloadUtils
import com.farshidrezaei.ccloudplus.core.utils.StorageUtils
import com.farshidrezaei.ccloudplus.data.model.Movie
import com.farshidrezaei.ccloudplus.data.model.Series
import com.farshidrezaei.ccloudplus.data.model.toFavoriteItem
import com.farshidrezaei.ccloudplus.player.VideoPlayerActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaActionSheet(
    item: Any,
    mediaType: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    val id: Int
    val title: String
    val image: String
    val year: String
    val imdb: String
    val videoUrl: String

    when (item) {
        is Movie -> {
            id = item.id
            title = item.title
            image = item.image
            year = item.year.toString()
            imdb = item.imdb.toString()
            videoUrl = item.sources.firstOrNull()?.url ?: ""
        }
        is Series -> {
            id = item.id
            title = item.title
            image = item.image
            year = item.year
            imdb = item.imdb
            videoUrl = ""
        }
        else -> return
    }

    val isFav = StorageUtils.isFavorite(context, id, mediaType)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF13151B),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.3f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header: Poster + Title + Year/Rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = image,
                    contentDescription = title,
                    modifier = Modifier
                        .size(width = 56.dp, height = 80.dp)
                        .clip(RoundedCornerShape(8.dp))
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = CCloudTypography.TitleMedium,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$year · امتیاز: $imdb",
                        style = CCloudTypography.BodySmall,
                        color = CCloudColors.TextSecondary
                    )
                }
            }

            Divider(color = CCloudColors.GlassBorder, thickness = 1.dp)

            // Actions for Movies
            if (videoUrl.isNotBlank()) {
                ActionRow(
                    icon = Icons.Default.PlayArrow,
                    title = "پخش در پلیر CCloud+",
                    tint = CCloudColors.AccentRed,
                    onClick = {
                        onDismiss()
                        val intent = Intent(context, VideoPlayerActivity::class.java).apply {
                            putExtra("video_url", videoUrl)
                            putExtra("video_title", title)
                            putExtra("media_id", id)
                            putExtra("media_type", mediaType)
                            putExtra("media_poster", image)
                        }
                        context.startActivity(intent)
                    }
                )

                ActionRow(
                    icon = Icons.Default.OpenInNew,
                    title = "پخش در پلیر خارجی (VLC / MX Player)",
                    tint = Color.White,
                    onClick = {
                        onDismiss()
                        try {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                setDataAndType(Uri.parse(videoUrl), "video/*")
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "پلیر خارجی یافت نشد", Toast.LENGTH_SHORT).show()
                        }
                    }
                )

                ActionRow(
                    icon = Icons.Default.Download,
                    title = "دانلود با مرورگر یا ADM",
                    tint = Color.White,
                    onClick = {
                        onDismiss()
                        DownloadUtils.openUrl(context, videoUrl)
                    }
                )

                ActionRow(
                    icon = Icons.Default.ContentCopy,
                    title = "کپی آدرس استریم / دانلود",
                    tint = Color.White,
                    onClick = {
                        onDismiss()
                        DownloadUtils.copyToClipboard(context, videoUrl)
                    }
                )
            }

            // Favorite action (both Movies and Series)
            ActionRow(
                icon = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                title = if (isFav) "حذف از نشان‌شده‌ها" else "افزودن به نشان‌شده‌ها",
                tint = if (isFav) CCloudColors.AccentRed else Color.White,
                onClick = {
                    onDismiss()
                    if (isFav) {
                        StorageUtils.removeFavorite(context, id, mediaType)
                        Toast.makeText(context, "از نشان‌شده‌ها حذف شد", Toast.LENGTH_SHORT).show()
                    } else {
                        val favItem = when (item) {
                            is Movie -> item.toFavoriteItem()
                            is Series -> item.toFavoriteItem()
                            else -> null
                        }
                        favItem?.let { StorageUtils.saveFavorite(context, it) }
                        Toast.makeText(context, "به نشان‌شده‌ها افزوده شد", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }
}

@Composable
private fun ActionRow(
    icon: ImageVector,
    title: String,
    tint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = title,
            style = CCloudTypography.BodyLarge,
            color = Color.White
        )
    }
}
