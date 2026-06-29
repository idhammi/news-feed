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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val getTopHeadlinesUseCase: GetTopHeadlinesUseCase,
    private val locationTracker: LocationTracker
) : ViewModel() {

    private val _countryName = MutableStateFlow<String?>(null)
    val countryName: StateFlow<String?> = _countryName.asStateFlow()

    private val _mapLocation = MutableStateFlow<GeoPoint?>(null)
    val mapLocation: StateFlow<GeoPoint?> = _mapLocation.asStateFlow()

    private val _country = MutableStateFlow<String?>(null)
    val articlesFlow: Flow<PagingData<Article>> = _country
        .flatMapLatest { country ->
            getTopHeadlinesUseCase(country)
        }
        .cachedIn(viewModelScope)

    fun fetchLocalNews() {
        viewModelScope.launch {
            val coords = locationTracker.getCurrentLocationCoordinates()
            if (coords != null) {
                updateMapLocation(coords.first, coords.second)
            }
        }
    }

    fun updateMapLocation(latitude: Double, longitude: Double) {
        _mapLocation.value = GeoPoint(latitude, longitude)
        viewModelScope.launch {
            val countryCode = locationTracker.getCountryCodeFromLocation(latitude, longitude)
            if (countryCode != null) {
                _country.value = countryCode
                _countryName.value =
                    Locale.Builder().setRegion(countryCode).build().displayCountry
            } else {
                _countryName.value = "Unknown Location"
            }
        }
    }

    fun fetchGlobalNews() {
        _country.value = null
        _countryName.value = null
    }
}
