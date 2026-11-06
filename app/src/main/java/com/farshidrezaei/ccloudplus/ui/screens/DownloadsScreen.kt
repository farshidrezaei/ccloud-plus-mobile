package com.farshidrezaei.ccloudplus.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.farshidrezaei.ccloudplus.core.theme.CinevexColors
import com.farshidrezaei.ccloudplus.core.theme.CinevexTypography
import com.farshidrezaei.ccloudplus.core.utils.DeviceUtils
import com.farshidrezaei.ccloudplus.player.VideoPlayerActivity
import com.farshidrezaei.ccloudplus.ui.components.GlassBox
import com.farshidrezaei.ccloudplus.ui.components.GlassButton
import java.io.File

data class LocalDownloadedItem(
    val file: File,
    val name: String,
    val sizeBytes: Long
)

@Composable
fun DownloadsScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val isTv = remember { DeviceUtils.isTv(context) }
    var downloadedFiles by remember { mutableStateOf<List<LocalDownloadedItem>>(emptyList()) }

    fun refreshFiles() {
        val cacheDir = File(context.cacheDir, "ccloud_video_cache")
        val files = mutableListOf<LocalDownloadedItem>()
        if (cacheDir.exists()) {
            cacheDir.listFiles()?.forEach { file ->
                if (file.isFile && file.length() > 1024 * 1024) { // > 1MB
                    files.add(
                        LocalDownloadedItem(
                            file = file,
                            name = file.name,
                            sizeBytes = file.length()
                        )
                    )
                }
            }
        }
        downloadedFiles = files
    }

    LaunchedEffect(Unit) {
        refreshFiles()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CinevexColors.BackgroundOled)
            .padding(
                start = 20.dp,
                end = 20.dp,
                top = 20.dp,
                bottom = if (isTv) 24.dp else 90.dp
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "دانلودها و کش محلی",
                    style = CinevexTypography.HeroTitle
                )

                if (downloadedFiles.isNotEmpty()) {
                    Text(
                        text = "پاکسازی همه",
                        style = CinevexTypography.LabelSmall,
                        color = CinevexColors.AccentRed,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (downloadedFiles.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            tint = CinevexColors.TextMuted,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "هیچ فایل دانلود شده یا کش محلی وجود ندارد",
                            style = CinevexTypography.TitleMedium,
                            color = CinevexColors.TextSecondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "فیلم‌ها و قسمت‌های پخش‌شده برای تماشای بدون وقفه و آفلاین به طور خودکار کش می‌شوند.",
                            style = CinevexTypography.LabelSmall,
                            color = CinevexColors.TextMuted
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(downloadedFiles) { item ->
                        val sizeMb = item.sizeBytes / (1024 * 1024)
                        GlassBox(
                            shape = RoundedCornerShape(14.dp),
                            backgroundColor = CinevexColors.GlassSurfaceElevated,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Folder,
                                        contentDescription = null,
                                        tint = CinevexColors.AccentRed,
                                        modifier = Modifier.size(28.dp)
                                    )

                                    Column {
                                        Text(
                                            text = item.name,
                                            style = CinevexTypography.TitleMedium,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = "$sizeMb مگابایت",
                                            style = CinevexTypography.LabelSmall,
                                            color = CinevexColors.TextSecondary
                                        )
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    GlassButton(
                                        text = "پخش",
                                        isPrimary = true,
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        },
                                        onClick = {
                                            val intent = Intent(context, VideoPlayerActivity::class.java).apply {
                                                putExtra("video_url", item.file.absolutePath)
                                                putExtra("video_title", item.name)
                                            }
                                            context.startActivity(intent)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
