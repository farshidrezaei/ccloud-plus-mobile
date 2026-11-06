package com.farshidrezaei.ccloudplus.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.farshidrezaei.ccloudplus.R
import com.farshidrezaei.ccloudplus.core.theme.CinevexColors
import com.farshidrezaei.ccloudplus.core.theme.CinevexTypography

@Composable
fun GlassSidebarNavigation(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(84.dp)
            .background(Color(0xF0090A0E))
            .border(width = 1.dp, color = CinevexColors.GlassBorder)
            .padding(vertical = 20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // App Logo
            Image(
                painter = painterResource(id = R.drawable.splash_logo),
                contentDescription = "CCloud+",
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
            )

            // Navigation Items
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                AppScreens.navigationItems.forEach { screen ->
                    val isSelected = currentRoute == screen.route
                    val interactionSource = remember { MutableInteractionSource() }
                    val isFocused by interactionSource.collectIsFocusedAsState()

                    val scale by animateFloatAsState(
                        targetValue = if (isFocused) 1.15f else 1.0f,
                        animationSpec = tween(150),
                        label = "sidebar_scale"
                    )

                    val tint by animateColorAsState(
                        targetValue = if (isSelected || isFocused) CinevexColors.AccentRed else CinevexColors.TextSecondary,
                        animationSpec = tween(150),
                        label = "sidebar_tint"
                    )

                    Box(
                        modifier = Modifier
                            .scale(scale)
                            .size(54.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelected) CinevexColors.GlassSurfaceElevated
                                else if (isFocused) CinevexColors.GlassSurfaceHover
                                else Color.Transparent
                            )
                            .border(
                                width = if (isFocused) 2.dp else if (isSelected) 1.dp else 0.dp,
                                color = if (isFocused) CinevexColors.GlassBorderFocus else CinevexColors.AccentRed,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                            .focusable(interactionSource = interactionSource),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            screen.icon?.let { icon ->
                                Icon(
                                    imageVector = icon,
                                    contentDescription = stringResource(screen.resourceId),
                                    tint = tint,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Settings Icon at bottom of Sidebar for TV
            val isSettingsSelected = currentRoute == AppScreens.Settings.route
            val settingsInteractionSource = remember { MutableInteractionSource() }
            val isSettingsFocused by settingsInteractionSource.collectIsFocusedAsState()
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (isSettingsSelected) CinevexColors.GlassSurfaceElevated
                        else if (isSettingsFocused) CinevexColors.GlassSurfaceHover
                        else Color.Transparent
                    )
                    .border(
                        width = if (isSettingsFocused) 2.dp else if (isSettingsSelected) 1.dp else 0.dp,
                        color = if (isSettingsFocused) CinevexColors.GlassBorderFocus else CinevexColors.AccentRed,
                        shape = RoundedCornerShape(14.dp)
                    )
                    .clickable(
                        interactionSource = settingsInteractionSource,
                        indication = null
                    ) {
                        navController.navigate(AppScreens.Settings.route)
                    }
                    .focusable(interactionSource = settingsInteractionSource),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "تنظیمات",
                    tint = if (isSettingsSelected || isSettingsFocused) CinevexColors.AccentRed else CinevexColors.TextSecondary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
