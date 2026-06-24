package id.idham.newsfeed.core.domain

import androidx.paging.PagingData
import id.idham.newsfeed.core.data.repository.NewsRepository
import id.idham.newsfeed.core.model.Article
import kotlinx.coroutines.flow.Flow

class GetTopHeadlinesUseCase(private val repository: NewsRepository) {
    operator fun invoke(country: String? = null): Flow<PagingData<Article>> {
        return repository.getTopHeadlines("general", country)
    }
}
