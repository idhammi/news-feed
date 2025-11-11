package id.idham.newsfeed.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import id.idham.newsfeed.core.designsystem.theme.enterTransition
import id.idham.newsfeed.core.designsystem.theme.exitTransition
import id.idham.newsfeed.core.designsystem.theme.popEnterTransition
import id.idham.newsfeed.core.designsystem.theme.popExitTransition
import id.idham.newsfeed.feature.detail.navigation.detailScreen
import id.idham.newsfeed.feature.detail.navigation.navigateToDetail
import id.idham.newsfeed.feature.home.navigation.HomeRoute
import id.idham.newsfeed.feature.home.navigation.homeScreen

@Composable
fun NewsFeedNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = modifier,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        popEnterTransition = popEnterTransition,
        popExitTransition = popExitTransition
    ) {
        homeScreen(onItemClicked = navController::navigateToDetail)
        detailScreen(onBackClick = { navController.popBackStack() })
    }
}
