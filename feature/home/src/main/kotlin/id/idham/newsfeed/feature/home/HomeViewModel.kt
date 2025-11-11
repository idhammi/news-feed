package id.idham.newsfeed.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import id.idham.newsfeed.core.domain.GetTopHeadlinesUseCase
import id.idham.newsfeed.core.model.Article
import kotlinx.coroutines.flow.Flow

class HomeViewModel(getTopHeadlinesUseCase: GetTopHeadlinesUseCase) : ViewModel() {

    val articlesFlow: Flow<PagingData<Article>> = getTopHeadlinesUseCase()
        .cachedIn(viewModelScope)
}
