package com.farshidrezaei.ccloudplus.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.farshidrezaei.ccloudplus.ui.screens.*

@Composable
fun AppNavigation(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = AppScreens.Home.route,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        composable(AppScreens.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(AppScreens.Movies.route) {
            MoviesScreen(navController = navController)
        }

        composable(AppScreens.Series.route) {
            SeriesScreen(navController = navController)
        }

        composable(AppScreens.Search.route) {
            SearchScreen(navController = navController)
        }

        composable(AppScreens.Favorites.route) {
            FavoritesScreen(navController = navController)
        }

        composable(AppScreens.Downloads.route) {
            DownloadsScreen(navController = navController)
        }

        composable(AppScreens.Settings.route) {
            SettingsScreen(navController = navController)
        }

        composable(AppScreens.About.route) {
            AboutScreen(navController = navController)
        }

        composable(
            route = AppScreens.Country.route,
            arguments = listOf(navArgument("countryId") { type = NavType.IntType; defaultValue = 0 })
        ) { backStackEntry ->
            val countryId = backStackEntry.arguments?.getInt("countryId") ?: 0
            CountryScreen(countryId = countryId, navController = navController)
        }

        composable(
            route = AppScreens.SingleMovie.route,
            arguments = listOf(navArgument("movieId") { type = NavType.IntType })
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt("movieId") ?: 0
            SingleMovieScreen(movieId = movieId, navController = navController)
        }

        composable(
            route = AppScreens.SingleSeries.route,
            arguments = listOf(navArgument("seriesId") { type = NavType.IntType })
        ) { backStackEntry ->
            val seriesId = backStackEntry.arguments?.getInt("seriesId") ?: 0
            SingleSeriesScreen(seriesId = seriesId, navController = navController)
        }
    }
}
