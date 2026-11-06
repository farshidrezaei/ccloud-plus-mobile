package com.farshidrezaei.ccloudplus.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.farshidrezaei.ccloudplus.core.network.DnsPreset
import com.farshidrezaei.ccloudplus.core.network.OkHttpClientProvider
import com.farshidrezaei.ccloudplus.core.theme.AppThemeMode
import com.farshidrezaei.ccloudplus.core.theme.CCloudColors
import com.farshidrezaei.ccloudplus.core.theme.CCloudTypography
import com.farshidrezaei.ccloudplus.core.theme.ThemeManager
import com.farshidrezaei.ccloudplus.core.utils.DeviceUtils
import com.farshidrezaei.ccloudplus.core.utils.StorageUtils
import com.farshidrezaei.ccloudplus.data.model.SubtitleSettings
import com.farshidrezaei.ccloudplus.data.model.VideoPlayerSettings
import com.farshidrezaei.ccloudplus.player.ExoPlayerManager
import com.farshidrezaei.ccloudplus.ui.components.GlassBox
import com.farshidrezaei.ccloudplus.ui.components.GlassButton
import com.farshidrezaei.ccloudplus.ui.navigation.AppScreens
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val isTv = remember { DeviceUtils.isTv(context) }

    var selectedThemeMode by remember { mutableStateOf(ThemeManager.currentThemeMode) }
    var showThemeDialog by remember { mutableStateOf(false) }

    var selectedDns by remember { mutableStateOf(OkHttpClientProvider.getCurrentPreset()) }
    var showDnsDialog by remember { mutableStateOf(false) }

    var videoSettings by remember { mutableStateOf(StorageUtils.loadVideoPlayerSettings(context)) }
    var subtitleSettings by remember { mutableStateOf(StorageUtils.loadSubtitleSettings(context)) }
    var showSubtitleDialog by remember { mutableStateOf(false) }

    var cacheSizeMb by remember { mutableStateOf(0L) }
    var watchedCount by remember { mutableStateOf(0) }

    fun calculateCache() {
        val cacheDir = File(context.cacheDir, "ccloud_video_cache")
        var totalBytes = 0L
        if (cacheDir.exists()) {
            cacheDir.walkTopDown().forEach {
                if (it.isFile) totalBytes += it.length()
            }
        }
        cacheSizeMb = totalBytes / (1024 * 1024)
        watchedCount = StorageUtils.loadAllWatchedEpisodes(context).size
    }

    LaunchedEffect(Unit) {
        calculateCache()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CCloudColors.BackgroundOled)
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = 12.dp,
                bottom = if (isTv) 24.dp else 32.dp
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with Back button
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(CCloudColors.GlassSurface)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "بازگشت",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "تنظیمات CCloud+",
                        style = CCloudTypography.TitleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }

            // 1. Theme Selection (ظاهر و تم)
            item {
                Text(
                    text = "ظاهر و تم",
                    style = CCloudTypography.TitleSmall.copy(fontWeight = FontWeight.Bold),
                    color = CCloudColors.AccentRed
                )
                Spacer(modifier = Modifier.height(6.dp))

                GlassBox(
                    shape = RoundedCornerShape(14.dp),
                    backgroundColor = CCloudColors.GlassSurfaceElevated,
                    onClick = { showThemeDialog = true },
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
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                tint = CCloudColors.AccentRed,
                                modifier = Modifier.size(22.dp)
                            )
                            Column {
                                Text(
                                    text = "حالت تم (رنگ‌بندی)",
                                    style = CCloudTypography.BodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = Color.White
                                )
                                Text(
                                    text = selectedThemeMode.titleFa,
                                    style = CCloudTypography.LabelSmall,
                                    color = CCloudColors.TextSecondary
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = CCloudColors.TextMuted
                        )
                    }
                }
            }

            // 2. Playback & Player Options (کیفیت و پخش)
            item {
                Text(
                    text = "پلیر و پخش ویدیو",
                    style = CCloudTypography.TitleSmall.copy(fontWeight = FontWeight.Bold),
                    color = CCloudColors.AccentRed
                )
                Spacer(modifier = Modifier.height(6.dp))

                GlassBox(
                    shape = RoundedCornerShape(14.dp),
                    backgroundColor = CCloudColors.GlassSurfaceElevated,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Subtitle Preferences Trigger
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showSubtitleDialog = true },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Subtitles,
                                    contentDescription = null,
                                    tint = CCloudColors.AccentRed,
                                    modifier = Modifier.size(22.dp)
                                )
                                Column {
                                    Text(
                                        text = "تنظیمات زیرنویس",
                                        style = CCloudTypography.BodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = Color.White
                                    )
                                    Text(
                                        text = "اندازه ${subtitleSettings.textSize.toInt()}sp • رنگ ${if (subtitleSettings.textColor == android.graphics.Color.YELLOW) "زرد" else "سفید"}",
                                        style = CCloudTypography.LabelSmall,
                                        color = CCloudColors.TextSecondary
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = CCloudColors.TextMuted
                            )
                        }

                        Divider(color = CCloudColors.GlassBorder, thickness = 1.dp)

                        // Seek duration toggle (10s vs 15s)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "مدت زمان پرش (ثانیه)",
                                    style = CCloudTypography.BodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = Color.White
                                )
                                Text(
                                    text = "${videoSettings.seekTimeSeconds} ثانیه با هر ضربه",
                                    style = CCloudTypography.LabelSmall,
                                    color = CCloudColors.TextSecondary
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf(10, 15, 30).forEach { sec ->
                                    val isCur = videoSettings.seekTimeSeconds == sec
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isCur) CCloudColors.AccentRed else CCloudColors.GlassSurface)
                                            .clickable {
                                                val updated = videoSettings.copy(seekTimeSeconds = sec)
                                                videoSettings = updated
                                                StorageUtils.saveVideoPlayerSettings(context, updated)
                                            }
                                            .padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Text(
                                            text = "$sec s",
                                            style = CCloudTypography.LabelSmall,
                                            color = if (isCur) Color.White else CCloudColors.TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. DNS Sanction Bypass (ضد تحریم)
            item {
                Text(
                    text = "شبکه و گذر از تحریم",
                    style = CCloudTypography.TitleSmall.copy(fontWeight = FontWeight.Bold),
                    color = CCloudColors.AccentRed
                )
                Spacer(modifier = Modifier.height(6.dp))

                GlassBox(
                    shape = RoundedCornerShape(14.dp),
                    backgroundColor = CCloudColors.GlassSurfaceElevated,
                    onClick = { showDnsDialog = true },
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
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Dns,
                                contentDescription = null,
                                tint = CCloudColors.AccentRed,
                                modifier = Modifier.size(22.dp)
                            )
                            Column {
                                Text(
                                    text = "دی‌ان‌اس ضدتحریم داخلی (DoH)",
                                    style = CCloudTypography.BodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = Color.White
                                )
                                Text(
                                    text = selectedDns.titleFa,
                                    style = CCloudTypography.LabelSmall,
                                    color = CCloudColors.TextSecondary
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = CCloudColors.TextMuted
                        )
                    }
                }
            }

            // 4. Cache & Storage Section
            item {
                Text(
                    text = "حافظه و پخش محلی",
                    style = CCloudTypography.TitleSmall.copy(fontWeight = FontWeight.Bold),
                    color = CCloudColors.AccentRed
                )
                Spacer(modifier = Modifier.height(6.dp))

                GlassBox(
                    shape = RoundedCornerShape(14.dp),
                    backgroundColor = CCloudColors.GlassSurfaceElevated,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "حجم کش ویدیوها",
                                    style = CCloudTypography.BodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = Color.White
                                )
                                Text(
                                    text = "$cacheSizeMb مگابایت استفاده شده",
                                    style = CCloudTypography.LabelSmall,
                                    color = CCloudColors.TextSecondary
                                )
                            }

                            GlassButton(
                                text = "پاکسازی",
                                isPrimary = false,
                                onClick = {
                                    ExoPlayerManager.releaseCache()
                                    val cacheDir = File(context.cacheDir, "ccloud_video_cache")
                                    if (cacheDir.exists()) cacheDir.deleteRecursively()
                                    calculateCache()
                                    Toast.makeText(context, "کش ویدیو با موفقیت پاک شد", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }

                        Divider(color = CCloudColors.GlassBorder, thickness = 1.dp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "تاریخچه قسمت‌های دیده‌شده",
                                    style = CCloudTypography.BodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = Color.White
                                )
                                Text(
                                    text = "$watchedCount قسمت ذخیره شده",
                                    style = CCloudTypography.LabelSmall,
                                    color = CCloudColors.TextSecondary
                                )
                            }

                            GlassButton(
                                text = "ریست",
                                isPrimary = false,
                                onClick = {
                                    StorageUtils.clearAllWatchedEpisodes(context)
                                    calculateCache()
                                    Toast.makeText(context, "تاریخچه مشاهده پاک شد", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            }

            // 5. About & Attribution Section
            item {
                Text(
                    text = "درباره و توسعه‌دهندگان",
                    style = CCloudTypography.TitleSmall.copy(fontWeight = FontWeight.Bold),
                    color = CCloudColors.AccentRed
                )
                Spacer(modifier = Modifier.height(6.dp))

                GlassBox(
                    shape = RoundedCornerShape(14.dp),
                    backgroundColor = CCloudColors.GlassSurfaceElevated,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "CCloud+ نسخه ۲.۰.۱",
                            style = CCloudTypography.TitleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "طراحی و توسعه‌یافته توسط فرشید رضایی",
                            style = CCloudTypography.BodyMedium,
                            color = CCloudColors.TextSecondary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "با تقدیر و سپاس ویژه از حسین پیرا (code3-dev) خالق و بنیان‌گذار اولیه پروژه ارزشمند CCloud برای جامعه متن‌باز فارسی.",
                                style = CCloudTypography.BodySmall,
                                color = CCloudColors.TextMuted,
                                lineHeight = 19.sp
                            )
                        }
                    }
                }
            }
        }

        // Theme Selector Dialog
        if (showThemeDialog) {
            AlertDialog(
                onDismissRequest = { showThemeDialog = false },
                containerColor = CCloudColors.SurfaceDark,
                title = {
                    Text(
                        text = "انتخاب حالت تم",
                        style = CCloudTypography.TitleMedium,
                        color = Color.White
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        AppThemeMode.entries.forEach { mode ->
                            val isCurrent = selectedThemeMode == mode
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isCurrent) CCloudColors.GlassSurfaceElevated else Color.Transparent)
                                    .clickable {
                                        selectedThemeMode = mode
                                        ThemeManager.setThemeMode(context, mode)
                                        showThemeDialog = false
                                        Toast.makeText(context, "تم به ${mode.titleFa} تغییر یافت", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = mode.titleFa,
                                    style = CCloudTypography.BodyMedium,
                                    color = if (isCurrent) CCloudColors.AccentRed else Color.White
                                )
                                if (isCurrent) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = CCloudColors.AccentRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showThemeDialog = false }) {
                        Text("بستن", color = CCloudColors.AccentRed)
                    }
                }
            )
        }

        // Subtitle Settings Dialog
        if (showSubtitleDialog) {
            AlertDialog(
                onDismissRequest = { showSubtitleDialog = false },
                containerColor = CCloudColors.SurfaceDark,
                title = {
                    Text(
                        text = "تنظیمات زیرنویس",
                        style = CCloudTypography.TitleMedium,
                        color = Color.White
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        // Font size
                        Text("اندازه قلم:", style = CCloudTypography.BodyMedium, color = Color.White)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(16f to "کوچک", 22f to "متوسط", 28f to "بزرگ").forEach { (sz, label) ->
                                val isCur = subtitleSettings.textSize == sz
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isCur) CCloudColors.AccentRed else CCloudColors.GlassSurface)
                                        .clickable {
                                            val updated = subtitleSettings.copy(textSize = sz)
                                            subtitleSettings = updated
                                            StorageUtils.saveSubtitleSettings(context, updated)
                                        }
                                        .padding(horizontal = 14.dp, vertical = 7.dp)
                                ) {
                                    Text(label, color = if (isCur) Color.White else CCloudColors.TextSecondary)
                                }
                            }
                        }

                        // Color
                        Text("رنگ متن:", style = CCloudTypography.BodyMedium, color = Color.White)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(android.graphics.Color.WHITE to "سفید", android.graphics.Color.YELLOW to "زرد").forEach { (clr, label) ->
                                val isCur = subtitleSettings.textColor == clr
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isCur) CCloudColors.AccentRed else CCloudColors.GlassSurface)
                                        .clickable {
                                            val updated = subtitleSettings.copy(textColor = clr)
                                            subtitleSettings = updated
                                            StorageUtils.saveSubtitleSettings(context, updated)
                                        }
                                        .padding(horizontal = 14.dp, vertical = 7.dp)
                                ) {
                                    Text(label, color = if (isCur) Color.White else CCloudColors.TextSecondary)
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSubtitleDialog = false }) {
                        Text("بستن", color = CCloudColors.AccentRed)
                    }
                }
            )
        }

        // DNS Selector Dialog
        if (showDnsDialog) {
            AlertDialog(
                onDismissRequest = { showDnsDialog = false },
                containerColor = CCloudColors.SurfaceDark,
                title = {
                    Text(
                        text = "انتخاب دی‌ان‌اس تحریم‌شکن (DoH)",
                        style = CCloudTypography.TitleMedium,
                        color = Color.White
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        DnsPreset.entries.forEach { preset ->
                            val isCurrent = selectedDns == preset
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isCurrent) CCloudColors.GlassSurfaceElevated else Color.Transparent)
                                    .clickable {
                                        selectedDns = preset
                                        OkHttpClientProvider.setDnsPreset(preset)
                                        showDnsDialog = false
                                        Toast.makeText(context, "دی‌ان‌اس به ${preset.titleFa} تغییر یافت", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = preset.titleFa,
                                    style = CCloudTypography.BodyMedium,
                                    color = if (isCurrent) CCloudColors.AccentRed else Color.White
                                )
                                if (isCurrent) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = CCloudColors.AccentRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showDnsDialog = false }) {
                        Text("بستن", color = CCloudColors.AccentRed)
                    }
                }
            )
        }
    }
}
