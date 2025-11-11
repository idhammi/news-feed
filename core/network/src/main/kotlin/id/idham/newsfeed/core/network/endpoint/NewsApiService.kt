package id.idham.newsfeed.core.network.endpoint

import id.idham.newsfeed.core.network.model.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("category") category: String,
        @Query("pageSize") pageSize: String,
        @Query("page") page: String,
    ): NewsResponse
}
