package id.idham.newsfeed.core.data.repository

import androidx.paging.PagingData
import id.idham.newsfeed.core.model.Article
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    fun getTopHeadlines(category: String, country: String? = null): Flow<PagingData<Article>>
}
