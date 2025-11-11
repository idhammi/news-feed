package id.idham.newsfeed.feature.detail.navigation

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import id.idham.newsfeed.core.model.Article
import id.idham.newsfeed.feature.detail.DetailScreen
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.reflect.typeOf

@Serializable
data class DetailRoute(val article: Article)

fun NavController.navigateToDetail(data: Article, navOptions: NavOptionsBuilder.() -> Unit = {}) {
    navigate(route = DetailRoute(data)) {
        navOptions()
    }
}

fun NavGraphBuilder.detailScreen(onBackClick: () -> Unit) {
    composable<DetailRoute>(
        typeMap = mapOf(typeOf<Article>() to ArticleType)
    ) {
        val args = it.toRoute<DetailRoute>()
        DetailScreen(
            article = args.article,
            onBackClick = onBackClick
        )
    }
}

val ArticleType = object : NavType<Article>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): Article? {
        return Json.decodeFromString(bundle.getString(key) ?: return null)
    }

    override fun parseValue(value: String): Article {
        return Json.decodeFromString(Uri.decode(value))
    }

    override fun serializeAsValue(value: Article): String {
        return Uri.encode(Json.encodeToString(value))
    }

    override fun put(bundle: Bundle, key: String, value: Article) {
        bundle.putString(key, Json.encodeToString(value))
    }
}
