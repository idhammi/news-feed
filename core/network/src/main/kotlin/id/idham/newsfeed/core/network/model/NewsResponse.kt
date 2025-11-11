package id.idham.newsfeed.core.network.model

import com.squareup.moshi.JsonClass
import id.idham.newsfeed.core.model.Article
import id.idham.newsfeed.core.model.News
import id.idham.newsfeed.core.model.Source

@JsonClass(generateAdapter = true)
data class NewsResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<ArticleResponse>?,
)

@JsonClass(generateAdapter = true)
data class ArticleResponse(
    val source: SourceResponse?,
    val author: String?,
    val title: String?,
    val description: String?,
    val url: String?,
    val urlToImage: String?,
    val publishedAt: String?,
    val content: String?,
)

@JsonClass(generateAdapter = true)
data class SourceResponse(
    val id: String?,
    val name: String?,
)

fun NewsResponse.asExternalModel(): News {
    return News(
        articles = this.articles?.map {
            Article(
                source = Source(
                    id = it.source?.id.orEmpty(),
                    name = it.source?.name.orEmpty(),
                ),
                author = it.author.orEmpty(),
                title = it.title.orEmpty(),
                description = it.description.orEmpty(),
                url = it.url.orEmpty(),
                urlToImage = it.urlToImage.orEmpty(),
                publishedAt = it.publishedAt.orEmpty(),
                content = it.content.orEmpty(),
            )
        } ?: emptyList()
    )
}
