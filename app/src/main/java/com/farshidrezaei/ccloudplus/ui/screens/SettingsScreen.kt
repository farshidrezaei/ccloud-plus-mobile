package com.farshidrezaei.ccloudplus.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.farshidrezaei.ccloudplus.core.config.AppConstants
import com.farshidrezaei.ccloudplus.ui.navigation.AppScreens
import com.farshidrezaei.ccloudplus.core.network.DnsPreset
import com.farshidrezaei.ccloudplus.core.network.OkHttpClientProvider
import com.farshidrezaei.ccloudplus.core.theme.CinevexColors
import com.farshidrezaei.ccloudplus.core.theme.CinevexTypography
import com.farshidrezaei.ccloudplus.core.utils.DeviceUtils
import com.farshidrezaei.ccloudplus.core.utils.StorageUtils
import com.farshidrezaei.ccloudplus.player.ExoPlayerManager
import com.farshidrezaei.ccloudplus.ui.components.GlassBox
import com.farshidrezaei.ccloudplus.ui.components.GlassButton
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val isTv = remember { DeviceUtils.isTv(context) }

    var selectedDns by remember { mutableStateOf(OkHttpClientProvider.getCurrentPreset()) }
    var showDnsDialog by remember { mutableStateOf(false) }

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
            .background(CinevexColors.BackgroundOled)
            .padding(
                start = 20.dp,
                end = 20.dp,
                top = 20.dp,
                bottom = if (isTv) 24.dp else 90.dp
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "تنظیمات برنامه",
                    style = CinevexTypography.HeroTitle
                )
            }

            // 0. Favorites & Bookmarks Shortcut
            item {
                Text(
                    text = "مجموعه من",
                    style = CinevexTypography.TitleMedium,
                    color = CinevexColors.AccentRed
                )
                Spacer(modifier = Modifier.height(8.dp))

                GlassBox(
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = CinevexColors.GlassSurfaceElevated,
                    onClick = { navController.navigate(AppScreens.Favorites.route) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = CinevexColors.AccentRed,
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    text = "فیلم‌ها و سریال‌های نشان‌شده",
                                    style = CinevexTypography.TitleMedium
                                )
                                Text(
                                    text = "مشاهده لیست علاقه‌مندی‌ها و ذخیره‌شده‌ها",
                                    style = CinevexTypography.LabelSmall,
                                    color = CinevexColors.TextSecondary
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = CinevexColors.TextMuted
                        )
                    }
                }
            }

            // 1. Anti-Sanction DNS Section
            item {
                Text(
                    text = "شبکه و گذر از تحریم",
                    style = CinevexTypography.TitleMedium,
                    color = CinevexColors.AccentRed
                )
                Spacer(modifier = Modifier.height(8.dp))

                GlassBox(
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = CinevexColors.GlassSurfaceElevated,
                    onClick = { showDnsDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
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
                                tint = CinevexColors.AccentRed,
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    text = "دی‌ان‌اس ضدتحریم داخلی",
                                    style = CinevexTypography.TitleMedium
                                )
                                Text(
                                    text = selectedDns.titleFa,
                                    style = CinevexTypography.LabelSmall,
                                    color = CinevexColors.TextSecondary
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = CinevexColors.TextMuted
                        )
                    }
                }
            }

            // 2. Cache & Storage Section
            item {
                Text(
                    text = "حافظه و پخش محلی",
                    style = CinevexTypography.TitleMedium,
                    color = CinevexColors.AccentRed
                )
                Spacer(modifier = Modifier.height(8.dp))

                GlassBox(
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = CinevexColors.GlassSurfaceElevated,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
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
                                    style = CinevexTypography.TitleMedium
                                )
                                Text(
                                    text = "$cacheSizeMb مگابایت استفاده شده",
                                    style = CinevexTypography.LabelSmall,
                                    color = CinevexColors.TextSecondary
                                )
                            }

                            GlassButton(
                                text = "پاکسازی کش",
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

                        Divider(color = CinevexColors.GlassBorder, thickness = 1.dp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "تاریخچه قسمت‌های دیده‌شده",
                                    style = CinevexTypography.TitleMedium
                                )
                                Text(
                                    text = "$watchedCount قسمت ذخیره شده",
                                    style = CinevexTypography.LabelSmall,
                                    color = CinevexColors.TextSecondary
                                )
                            }

                            GlassButton(
                                text = "ریست قسمت‌ها",
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

            // 3. About & Attribution Section
            item {
                Text(
                    text = "درباره و توسعه‌دهندگان",
                    style = CinevexTypography.TitleMedium,
                    color = CinevexColors.AccentRed
                )
                Spacer(modifier = Modifier.height(8.dp))

                GlassBox(
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = CinevexColors.GlassSurfaceElevated,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "CCloud+ (نسخه ۲.۰.۰)",
                            style = CinevexTypography.TitleLarge
                        )
                        Text(
                            text = "توسعه‌یافته توسط فرشید رضایی",
                            style = CinevexTypography.BodyMedium,
                            color = CinevexColors.TextSecondary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Heartfelt attribution to Hossein Pira
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "با تقدیر و سپاس ویژه از حسین پیرا (code3-dev) خالق و بنیان‌گذار اولیه پروژه ارزشمند CCloud برای جامعه متن‌باز فارسی.",
                                style = CinevexTypography.BodyMedium,
                                color = CinevexColors.TextMuted,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }
        }

        // DNS Selector Dialog
        if (showDnsDialog) {
            AlertDialog(
                onDismissRequest = { showDnsDialog = false },
                containerColor = CinevexColors.SurfaceDark,
                title = {
                    Text(
                        text = "انتخاب دی‌ان‌اس تحریم‌شکن (DoH)",
                        style = CinevexTypography.TitleLarge,
                        color = Color.White
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        DnsPreset.values().forEach { preset ->
                            val isCurrent = selectedDns == preset
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isCurrent) CinevexColors.GlassSurfaceElevated else Color.Transparent)
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
                                    style = CinevexTypography.BodyMedium,
                                    color = if (isCurrent) CinevexColors.AccentRed else Color.White
                                )
                                if (isCurrent) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = CinevexColors.AccentRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showDnsDialog = false }) {
                        Text(text = "بستن", color = CinevexColors.AccentRed)
                    }
                }
            )
        }
    }
}
