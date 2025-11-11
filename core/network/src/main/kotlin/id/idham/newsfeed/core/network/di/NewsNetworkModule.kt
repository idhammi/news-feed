package id.idham.newsfeed.core.network.di

import id.idham.newsfeed.core.network.BuildConfig
import id.idham.newsfeed.core.network.endpoint.NewsApiService
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val newsNetworkModule = module {
    single<NewsApiService> {
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(get<OkHttpClient>())
            .addConverterFactory(get<MoshiConverterFactory>())
            .build()
            .create(NewsApiService::class.java)
    }
}
