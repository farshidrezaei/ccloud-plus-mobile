package com.farshidrezaei.ccloudplus

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.farshidrezaei.ccloudplus.core.theme.AppThemeMode
import com.farshidrezaei.ccloudplus.core.theme.CCloudColors
import com.farshidrezaei.ccloudplus.core.theme.ThemeManager
import com.farshidrezaei.ccloudplus.core.utils.DeviceUtils
import com.farshidrezaei.ccloudplus.ui.navigation.AppNavigation
import com.farshidrezaei.ccloudplus.ui.navigation.AppScreens
import com.farshidrezaei.ccloudplus.ui.navigation.GlassBottomNavigation
import com.farshidrezaei.ccloudplus.ui.navigation.GlassSidebarNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        if (!DeviceUtils.isTv(this)) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            enableEdgeToEdge()
        }
        
        setContent {
            MainApp()
        }
    }
}

@Composable
fun MainApp() {
    val context = LocalContext.current
    val isTv = remember { DeviceUtils.isTv(context) }
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isDark = when (ThemeManager.currentThemeMode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.DARK -> true
        AppThemeMode.LIGHT -> false
    }

    val systemUiController = rememberSystemUiController()
    LaunchedEffect(isDark) {
        systemUiController.setStatusBarColor(
            color = Color.Transparent,
            darkIcons = !isDark
        )
        systemUiController.setNavigationBarColor(
            color = Color.Transparent,
            darkIcons = !isDark
        )
    }

    val currentScreen = remember(currentRoute) {
        when {
            currentRoute?.startsWith("single_movie") == true -> AppScreens.SingleMovie
            currentRoute?.startsWith("single_series") == true -> AppScreens.SingleSeries
            currentRoute?.startsWith("country") == true -> AppScreens.Country
            currentRoute == "favorites" -> AppScreens.Favorites
            currentRoute == "about" -> AppScreens.About
            currentRoute == "downloads" -> AppScreens.Downloads
            currentRoute == "settings" -> AppScreens.Settings
            else -> AppScreens.navigationItems.find { it.route == currentRoute } ?: AppScreens.Home
        }
    }

    // Enforce 100% Native RTL Layout
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isDark) CCloudColors.BackgroundOled else CCloudColors.BackgroundLight)
        ) {
            if (isTv) {
                Row(modifier = Modifier.fillMaxSize()) {
                    if (currentScreen.showSidebar) {
                        GlassSidebarNavigation(navController = navController)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .statusBarsPadding()
                            .navigationBarsPadding()
                    ) {
                        AppNavigation(navController = navController)
                    }
                }
            } else {
                Scaffold(
                    containerColor = if (isDark) CCloudColors.BackgroundOled else CCloudColors.BackgroundLight,
                    contentWindowInsets = WindowInsets.statusBars,
                    bottomBar = {
                        if (currentScreen.showBottomBar) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .navigationBarsPadding()
                            ) {
                                GlassBottomNavigation(navController = navController)
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                top = innerPadding.calculateTopPadding(),
                                bottom = innerPadding.calculateBottomPadding()
                            )
                    ) {
                        AppNavigation(navController = navController)
                    }
                }
            }
        }
    }
}
