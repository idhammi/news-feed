package id.idham.newsfeed.core.network.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import id.idham.newsfeed.core.network.BuildConfig
import id.idham.newsfeed.core.network.interceptor.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.converter.moshi.MoshiConverterFactory

val networkModule = module {
    single<AuthInterceptor> {
        AuthInterceptor(BuildConfig.API_KEY)
    }

    single<HttpLoggingInterceptor> {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    single<OkHttpClient> {
        OkHttpClient
            .Builder()
            .addInterceptor(get<AuthInterceptor>())
            .addInterceptor(get<HttpLoggingInterceptor>())
            .build()
    }

    single<Moshi> {
        Moshi
            .Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    single<MoshiConverterFactory> {
        MoshiConverterFactory.create(get<Moshi>())
    }
}
