package id.idham.newsfeed.core.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val requestWithAuth = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer $apiKey")
            .build()

        return chain.proceed(requestWithAuth)
    }
}
