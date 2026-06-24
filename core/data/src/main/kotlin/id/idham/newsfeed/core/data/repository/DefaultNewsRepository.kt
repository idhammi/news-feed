package id.idham.newsfeed.core.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import id.idham.newsfeed.core.data.source.NewsRemoteMediator
import id.idham.newsfeed.core.database.AppDatabase
import id.idham.newsfeed.core.database.toArticle
import id.idham.newsfeed.core.model.Article
import id.idham.newsfeed.core.network.endpoint.NewsApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DefaultNewsRepository(
    private val api: NewsApiService,
    private val db: AppDatabase,
) : NewsRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getTopHeadlines(category: String): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
                initialLoadSize = PAGE_SIZE
            ),
            remoteMediator = NewsRemoteMediator(
                db = db,
                api = api,
                category = category
            ),
            pagingSourceFactory = { db.newsDao().pagingSource(category) }
        ).flow.map { pagingData ->
            pagingData.map { it.toArticle() }
        }
    }

    companion object {
        private const val PAGE_SIZE = 20
    }
}
