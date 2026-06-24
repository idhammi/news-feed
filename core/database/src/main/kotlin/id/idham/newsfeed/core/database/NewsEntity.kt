package id.idham.newsfeed.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import id.idham.newsfeed.core.model.Article
import id.idham.newsfeed.core.model.Source

@Entity(tableName = "article")
data class ArticleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val category: String,
    val sourceId: String,
    val sourceName: String,
    val author: String,
    val title: String,
    val description: String,
    val url: String,
    val urlToImage: String,
    val publishedAt: String,
    val content: String,
)

fun List<Article>.toEntity(category: String) = map {
    ArticleEntity(
        category = category,
        sourceId = it.source.id,
        sourceName = it.source.name,
        author = it.author,
        title = it.title,
        description = it.description,
        url = it.url,
        urlToImage = it.urlToImage,
        publishedAt = it.publishedAt,
        content = it.content,
    )
}

fun ArticleEntity.toArticle() = Article(
    source = Source(id = sourceId, name = sourceName),
    author = author,
    title = title,
    description = description,
    url = url,
    urlToImage = urlToImage,
    publishedAt = publishedAt,
    content = content,
)
