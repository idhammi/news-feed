package id.idham.newsfeed.core.data.location

interface LocationTracker {
    suspend fun getCurrentCountryCode(): String?
}
