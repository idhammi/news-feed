package id.idham.newsfeed.core.data.location

interface LocationTracker {
    suspend fun getCountryCodeFromLocation(latitude: Double, longitude: Double): String?
    suspend fun getCurrentLocationCoordinates(): Pair<Double, Double>?
}
