package id.idham.newsfeed.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import id.idham.newsfeed.core.data.source.NewsPagingSource
import id.idham.newsfeed.core.model.Article
import id.idham.newsfeed.core.network.endpoint.NewsApiService
import kotlinx.coroutines.flow.Flow

class DefaultNewsRepository(
    private val api: NewsApiService,
) : NewsRepository {

    override fun getTopHeadlines(category: String): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
                initialLoadSize = PAGE_SIZE
            ),
            pagingSourceFactory = { NewsPagingSource(api, category) }
        ).flow
    }

    companion object {
        private const val PAGE_SIZE = 20
    }
}
