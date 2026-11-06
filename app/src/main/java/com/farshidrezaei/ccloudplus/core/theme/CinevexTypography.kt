package com.farshidrezaei.ccloudplus.core.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.farshidrezaei.ccloudplus.R

val VazirmatnFamily = FontFamily(
    Font(R.font.vazirmatn_thin, FontWeight.Thin),
    Font(R.font.vazirmatn_extralight, FontWeight.ExtraLight),
    Font(R.font.vazirmatn_light, FontWeight.Light),
    Font(R.font.vazirmatn_regular, FontWeight.Normal),
    Font(R.font.vazirmatn_medium, FontWeight.Medium),
    Font(R.font.vazirmatn_semibold, FontWeight.SemiBold),
    Font(R.font.vazirmatn_bold, FontWeight.Bold),
    Font(R.font.vazirmatn_extrabold, FontWeight.ExtraBold),
    Font(R.font.vazirmatn_black, FontWeight.Black)
)

object CinevexTypography {
    val HeroTitle = TextStyle(
        fontFamily = VazirmatnFamily,
        fontWeight = FontWeight.Black,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        color = CinevexColors.TextPrimary
    )
    
    val TitleLarge = TextStyle(
        fontFamily = VazirmatnFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        color = CinevexColors.TextPrimary
    )

    val TitleMedium = TextStyle(
        fontFamily = VazirmatnFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        color = CinevexColors.TextPrimary
    )

    val BodyLarge = TextStyle(
        fontFamily = VazirmatnFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 24.sp,
        color = CinevexColors.TextPrimary
    )

    val BodyMedium = TextStyle(
        fontFamily = VazirmatnFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 20.sp,
        color = CinevexColors.TextSecondary
    )

    val LabelSmall = TextStyle(
        fontFamily = VazirmatnFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        color = CinevexColors.TextMuted
    )
    
    val ButtonText = TextStyle(
        fontFamily = VazirmatnFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        color = CinevexColors.TextPrimary
    )
    
    val BadgeText = TextStyle(
        fontFamily = VazirmatnFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        lineHeight = 14.sp
    )
}
