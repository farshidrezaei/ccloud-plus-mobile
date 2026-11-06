package com.farshidrezaei.ccloudplus

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.farshidrezaei.ccloudplus.core.theme.CinevexColors
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

    val systemUiController = rememberSystemUiController()
    LaunchedEffect(Unit) {
        systemUiController.setStatusBarColor(
            color = androidx.compose.ui.graphics.Color.Transparent,
            darkIcons = false
        )
        systemUiController.setNavigationBarColor(
            color = androidx.compose.ui.graphics.Color.Transparent,
            darkIcons = false
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
            else -> AppScreens.navigationItems.find { it.route == currentRoute } ?: AppScreens.Movies
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CinevexColors.BackgroundOled)
    ) {
        if (isTv) {
            Row(modifier = Modifier.fillMaxSize()) {
                if (currentScreen.showSidebar && currentRoute != AppScreens.Splash.route) {
                    GlassSidebarNavigation(navController = navController)
                }
                Box(modifier = Modifier.weight(1f)) {
                    AppNavigation(navController = navController)
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                AppNavigation(navController = navController)

                if (currentScreen.showBottomBar && currentRoute != AppScreens.Splash.route) {
                    Box(modifier = Modifier.align(androidx.compose.ui.Alignment.BottomCenter)) {
                        GlassBottomNavigation(navController = navController)
                    }
                }
            }
        }
    }
}
