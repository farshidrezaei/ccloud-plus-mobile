package com.farshidrezaei.ccloudplus.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.farshidrezaei.ccloudplus.core.error.AppError
import com.farshidrezaei.ccloudplus.core.theme.CinevexColors
import com.farshidrezaei.ccloudplus.core.theme.CinevexTypography

@Composable
fun ErrorStateView(
    error: AppError?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val message = error?.messagePersian ?: "خطایی در دریافت اطلاعات رخ داده است."

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        GlassBox(
            modifier = Modifier.fillMaxWidth(0.9f),
            backgroundColor = CinevexColors.GlassSurfaceElevated
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WifiOff,
                    contentDescription = null,
                    tint = CinevexColors.AccentRed,
                    modifier = Modifier.size(52.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "ارتباط با مشکل مواجه شد",
                    style = CinevexTypography.TitleLarge,
                    color = CinevexColors.TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = message,
                    style = CinevexTypography.BodyMedium,
                    textAlign = TextAlign.Center,
                    color = CinevexColors.TextSecondary
                )

                if (error?.technicalMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "کد خطا: ${error.technicalMessage}",
                        style = CinevexTypography.LabelSmall,
                        color = CinevexColors.TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                GlassButton(
                    text = "تلاش مجدد",
                    isPrimary = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    onClick = onRetry
                )
            }
        }
    }
}
