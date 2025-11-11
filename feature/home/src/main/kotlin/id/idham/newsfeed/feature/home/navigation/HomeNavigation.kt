package id.idham.newsfeed.feature.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import id.idham.newsfeed.core.model.Article
import id.idham.newsfeed.feature.home.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

fun NavGraphBuilder.homeScreen(
    onItemClicked: (Article) -> Unit
) {
    composable<HomeRoute> {
        HomeScreen(onItemClicked = onItemClicked)
    }
}
