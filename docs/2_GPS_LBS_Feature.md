# GPS, LBS & Interactive Map Feature

This project implements mobile location sensors and an interactive map to deliver localized news content based on the user's current physical location or any dropped pin on the map.

## 1. Feature Description
The application accesses the device's location when the user navigates to the "Local News" tab. It retrieves the current coordinates and adjusts the news feed. Furthermore, users can interact with an embedded, API-key-free OpenStreetMap to tap on any country in the world and instantly pull breaking news for that region.

## 2. Technical Implementation

- **Location Library**: `FusedLocationProviderClient` from Google Play Services.
- **Map Library**: `Osmdroid` (OpenStreetMap).
- **Reverse Geocoding**: Native Android `Geocoder`.
- **Permissions Required**: 
  - `ACCESS_COARSE_LOCATION`
  - `ACCESS_FINE_LOCATION`
- **Component**: `LocationTracker` & `OsmMapView`

### Code Sample: Reverse Geocoding
Here is the core logic inside the project responsible for converting a tapped map coordinate into a specific Country Code using the native Geocoder:

```kotlin
    override suspend fun getCountryCodeFromLocation(latitude: Double, longitude: Double): String? {
        return suspendCancellableCoroutine { cont ->
            val geocoder = Geocoder(context, Locale.getDefault())
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                        val countryCode = addresses.firstOrNull()?.countryCode?.lowercase()
                        cont.resume(countryCode)
                    }
                } else {
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                    val countryCode = addresses?.firstOrNull()?.countryCode?.lowercase()
                    cont.resume(countryCode)
                }
            } catch (e: Exception) {
                cont.resume(null)
            }
        }
    }
```

### Workflow:
1. **Permission Check**: The application verifies if location permissions have been granted via a Compose dialog.
2. **Coordinate Retrieval**: `LocationTracker` retrieves the device's current Latitude and Longitude via GPS, or users tap a point on the map.
3. **Reverse Geocoding**: The coordinates are processed using Android's native `Geocoder` to obtain the physical address.
4. **Country Code Extraction**: The "Country Code" is extracted from the address object (e.g., `id` for Indonesia or `us` for the United States).
5. **Content Fetching**: The country code is passed as a parameter (`country=id`) to the NewsAPI endpoint to localize the news feed.
6. **Dynamic UI**: The map utilizes Jetpack Compose `AnimatedVisibility` bound to the news list's scroll state, gracefully collapsing when the user scrolls down to read articles.

## 3. Implementation Screenshots

| Permission Request Dialog          | Interactive Map Feed                 |
|------------------------------------|--------------------------------------|
| ![Permission Dialog](images/4.png) | ![Interactive Map Feed](images/5.png)|
