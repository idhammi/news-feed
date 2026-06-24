# GPS & LBS (Location Based Services) Feature

This project implements mobile location sensors to deliver localized news content based on the user's current physical location.

## 1. Feature Description
The application accesses the device's location when the user navigates to the "Local News" tab. It retrieves the current country code and adjusts the news feed accordingly.

## 2. Technical Implementation

- **Library**: `FusedLocationProviderClient` from Google Play Services.
- **Permissions Required**: 
  - `ACCESS_COARSE_LOCATION`
  - `ACCESS_FINE_LOCATION`
- **Component**: `LocationTracker`

### Workflow:
1. **Permission Check**: The application verifies if location permissions have been granted via a Compose dialog.
2. **Coordinate Retrieval**: `LocationTracker` retrieves the device's current Latitude and Longitude.
3. **Reverse Geocoding**: The coordinates are processed using Android's native `Geocoder` to obtain the physical address.
4. **Country Code Extraction**: The "Country Code" is extracted from the address object (e.g., `ID` for Indonesia or `US` for the United States).
5. **Content Fetching**: The country code is passed as a parameter (`country=id`) to the NewsAPI endpoint to localize the news feed.

## 3. Implementation Screenshots

**GPS Location permission request dialog:**
![Permission Dialog](images/4.png)

**Home screen UI displaying the localized news feed:**
![Localized News Feed](images/5.png)
