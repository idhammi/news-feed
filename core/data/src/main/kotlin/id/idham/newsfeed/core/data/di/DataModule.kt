package id.idham.newsfeed.core.data.di

import android.content.Context
import com.google.android.gms.location.LocationServices
import id.idham.newsfeed.core.data.location.DefaultLocationTracker
import id.idham.newsfeed.core.data.location.LocationTracker
import id.idham.newsfeed.core.data.repository.DefaultNewsRepository
import id.idham.newsfeed.core.data.repository.NewsRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataModule = module {
    singleOf(::DefaultNewsRepository) { bind<NewsRepository>() }
    single { LocationServices.getFusedLocationProviderClient(get<Context>()) }
    singleOf(::DefaultLocationTracker) { bind<LocationTracker>() }
}
