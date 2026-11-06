package com.farshidrezaei.ccloudplus.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.farshidrezaei.ccloudplus.R

sealed class AppScreens(
    val route: String,
    @StringRes val resourceId: Int,
    val icon: ImageVector? = null,
    val showBottomBar: Boolean = true,
    val showSidebar: Boolean = true
) {
    data object Splash : AppScreens(
        route = "splash",
        resourceId = R.string.app_name,
        showBottomBar = false,
        showSidebar = false
    )

    data object Home : AppScreens(
        route = "home",
        resourceId = R.string.home,
        icon = Icons.Default.Home
    )

    data object Movies : AppScreens(
        route = "movies",
        resourceId = R.string.movies,
        icon = Icons.Default.Movie
    )

    data object Series : AppScreens(
        route = "series",
        resourceId = R.string.series,
        icon = Icons.Default.Tv
    )

    data object Search : AppScreens(
        route = "search",
        resourceId = R.string.search,
        icon = Icons.Default.Search
    )

    data object Favorites : AppScreens(
        route = "favorites",
        resourceId = R.string.favorites,
        icon = Icons.Default.Favorite,
        showBottomBar = true,
        showSidebar = true
    )

    data object Downloads : AppScreens(
        route = "downloads",
        resourceId = R.string.downloads,
        icon = Icons.Default.Download,
        showBottomBar = false,
        showSidebar = false
    )

    data object Settings : AppScreens(
        route = "settings",
        resourceId = R.string.settings,
        icon = Icons.Default.Settings,
        showBottomBar = false,
        showSidebar = false
    )

    data object SingleMovie : AppScreens(
        route = "single_movie/{movieId}",
        resourceId = R.string.movie_details,
        showBottomBar = false,
        showSidebar = false
    ) {
        fun createRoute(movieId: Int) = "single_movie/$movieId"
    }

    data object SingleSeries : AppScreens(
        route = "single_series/{seriesId}",
        resourceId = R.string.series_details,
        showBottomBar = false,
        showSidebar = false
    ) {
        fun createRoute(seriesId: Int) = "single_series/$seriesId"
    }

    data object Country : AppScreens(
        route = "country/{countryId}",
        resourceId = R.string.country,
        showBottomBar = false,
        showSidebar = false
    ) {
        fun createRoute(countryId: Int) = "country/$countryId"
    }

    data object About : AppScreens(
        route = "about",
        resourceId = R.string.about,
        showBottomBar = false,
        showSidebar = false
    )

    companion object {
        val navigationItems = listOf(
            Home,
            Movies,
            Series,
            Search,
            Favorites
        )
    }
}
