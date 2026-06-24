package id.idham.newsfeed.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import id.idham.newsfeed.core.data.location.LocationTracker
import id.idham.newsfeed.core.domain.GetTopHeadlinesUseCase
import id.idham.newsfeed.core.model.Article
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val getTopHeadlinesUseCase: GetTopHeadlinesUseCase,
    private val locationTracker: LocationTracker
) : ViewModel() {

    private val _country = MutableStateFlow<String?>(null)

    val articlesFlow: Flow<PagingData<Article>> = _country
        .flatMapLatest { country ->
            getTopHeadlinesUseCase(country)
        }
        .cachedIn(viewModelScope)

    fun fetchLocalNews() {
        viewModelScope.launch {
            val countryCode = locationTracker.getCurrentCountryCode()
            if (countryCode != null) {
                _country.value = countryCode
            }
        }
    }

    fun fetchGlobalNews() {
        _country.value = null
    }
}
