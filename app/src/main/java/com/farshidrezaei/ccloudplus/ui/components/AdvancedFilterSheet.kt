package com.farshidrezaei.ccloudplus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.farshidrezaei.ccloudplus.core.theme.CCloudColors
import com.farshidrezaei.ccloudplus.core.theme.CCloudTypography
import com.farshidrezaei.ccloudplus.data.model.AdvancedFilter
import com.farshidrezaei.ccloudplus.data.model.AudioType
import com.farshidrezaei.ccloudplus.data.model.ReleaseEra

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedFilterSheet(
    initialFilter: AdvancedFilter,
    onApply: (AdvancedFilter) -> Unit,
    onDismiss: () -> Unit
) {
    var minImdb by remember { mutableStateOf(initialFilter.minImdb) }
    var selectedEra by remember { mutableStateOf(initialFilter.era) }
    var selectedAudio by remember { mutableStateOf(initialFilter.audioType) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = CCloudColors.SurfaceDark,
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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "فیلتر پیشرفته محتوا",
                    style = CCloudTypography.TitleLarge
                )
                Text(
                    text = "بازنشانی",
                    style = CCloudTypography.LabelSmall,
                    color = CCloudColors.AccentRed,
                    modifier = Modifier.clickable {
                        minImdb = 0f
                        selectedEra = ReleaseEra.ALL
                        selectedAudio = AudioType.ALL
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // IMDb Slider
            Text(
                text = "حداقل امتیاز IMDb: ${if (minImdb > 0f) String.format("%.1f+", minImdb) else "همه"}",
                style = CCloudTypography.TitleMedium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Slider(
                value = minImdb,
                onValueChange = { minImdb = it },
                valueRange = 0f..9f,
                steps = 17, // increments of 0.5
                colors = SliderDefaults.colors(
                    thumbColor = CCloudColors.AccentRed,
                    activeTrackColor = CCloudColors.AccentRed,
                    inactiveTrackColor = Color.White.copy(alpha = 0.15f)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Release Era
            Text(
                text = "دوره انتشار",
                style = CCloudTypography.TitleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ReleaseEra.values()) { era ->
                    val isSelected = selectedEra == era
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) CCloudColors.AccentRed
                                else CCloudColors.GlassSurfaceElevated
                            )
                            .border(
                                1.dp,
                                if (isSelected) CCloudColors.AccentRed else CCloudColors.GlassBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedEra = era }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = era.titleFa,
                            style = CCloudTypography.LabelSmall,
                            color = if (isSelected) Color.White else CCloudColors.TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Audio & Dubbing
            Text(
                text = "صوت و دوبله",
                style = CCloudTypography.TitleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AudioType.values().forEach { audio ->
                    val isSelected = selectedAudio == audio
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) CCloudColors.AccentRed
                                else CCloudColors.GlassSurfaceElevated
                            )
                            .border(
                                1.dp,
                                if (isSelected) CCloudColors.AccentRed else CCloudColors.GlassBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedAudio = audio }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = audio.titleFa,
                            style = CCloudTypography.LabelSmall,
                            color = if (isSelected) Color.White else CCloudColors.TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // Apply Button
            GlassButton(
                text = "اعمال فیلترها",
                isPrimary = true,
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onApply(
                        AdvancedFilter(
                            minImdb = minImdb,
                            era = selectedEra,
                            audioType = selectedAudio
                        )
                    )
                    onDismiss()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
