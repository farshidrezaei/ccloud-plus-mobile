package com.farshidrezaei.ccloudplus.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.farshidrezaei.ccloudplus.core.theme.CCloudColors

@Composable
fun GlassBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    backgroundColor: Color = CCloudColors.GlassSurface,
    borderColor: Color = CCloudColors.GlassBorder,
    borderWidth: Dp = 1.dp,
    enableDpadFocus: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (enableDpadFocus && isFocused) 1.05f else 1.0f,
        animationSpec = tween(durationMillis = 200),
        label = "glass_scale"
    )

    val currentBorderColor = if (enableDpadFocus && isFocused) {
        CCloudColors.GlassBorderFocus
    } else {
        borderColor
    }

    val currentBorderWidth = if (enableDpadFocus && isFocused) 2.dp else borderWidth

    Box(
        modifier = modifier
            .scale(scale)
            .then(
                if (enableDpadFocus && isFocused) {
                    Modifier.shadow(16.dp, shape, spotColor = CCloudColors.AccentRedGlow)
                } else Modifier
            )
            .clip(shape)
            .background(backgroundColor)
            .border(currentBorderWidth, currentBorderColor, shape)
            .then(
                if (onClick != null) {
                    Modifier
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = onClick
                        )
                        .focusable(enabled = enableDpadFocus, interactionSource = interactionSource)
                } else if (enableDpadFocus) {
                    Modifier.focusable(interactionSource = interactionSource)
                } else Modifier
            ),
        content = content
    )
}

@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.06f else 1.0f,
        animationSpec = tween(180),
        label = "btn_scale"
    )

    val bgBrush = if (isPrimary) {
        Brush.horizontalGradient(
            listOf(CCloudColors.AccentRed, CCloudColors.AccentRedHover)
        )
    } else {
        Brush.horizontalGradient(
            listOf(CCloudColors.GlassSurfaceElevated, CCloudColors.GlassSurface)
        )
    }

    val border = if (isFocused) {
        CCloudColors.GlassBorderFocus
    } else if (isPrimary) {
        CCloudColors.AccentRed
    } else {
        CCloudColors.GlassBorder
    }

    Box(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(bgBrush)
            .border(if (isFocused) 2.dp else 1.dp, border, RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .focusable(interactionSource = interactionSource)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.foundation.layout.Row(
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            leadingIcon?.invoke()
            if (leadingIcon != null) {
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(horizontal = 4.dp))
            }
            androidx.compose.material3.Text(
                text = text,
                style = com.farshidrezaei.ccloudplus.core.theme.CCloudTypography.ButtonText,
                color = CCloudColors.TextPrimary
            )
        }
    }
}
