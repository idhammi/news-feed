# Network & API Implementation

This project implements client-server architecture using RESTful APIs, optimized for mobile network conditions.

## 1. API Integration
Network requests are handled using the `Retrofit2` and `OkHttp3` libraries.

- **Data Provider**: NewsAPI (`https://newsapi.org`)
- **Method**: `HTTP GET` on the `/v2/top-headlines` endpoint
- **Response Format**: JSON

### Code Sample
Here is the Retrofit API interface implementation:

```kotlin
interface NewsApi {
    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("category") category: String,
        @Query("country") country: String? = "us",
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): NewsResponse
}
```

## 2. Network Optimization
To optimize cellular data usage and maintain application responsiveness, the following mechanisms are utilized:

1. **Pagination (Paging 3)**: Data is fetched in increments of 20 articles per page. As the user scrolls towards the bottom of the list, the application automatically requests the next page in the background.
2. **Image Caching (Coil)**: Asynchronous image loading prevents redundant network requests by caching article thumbnails locally.
3. **Network Interception (Chucker)**: In debug builds, the *Chucker* library intercepts and logs all API transactions. This facilitates on-device network debugging and error tracking.

## 3. Offline Handling
In the event of network loss or insufficient cellular data:
- The data layer presents a UI warning indicating the loss of connectivity without causing an application crash.
- Due to the Room Database caching implementation (SSOT pattern), previously fetched news articles remain accessible for offline reading.
