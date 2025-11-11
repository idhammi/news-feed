package id.idham.newsfeed.core.data.di

import id.idham.newsfeed.core.data.repository.DefaultNewsRepository
import id.idham.newsfeed.core.data.repository.NewsRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataModule = module {
    singleOf(::DefaultNewsRepository) { bind<NewsRepository>() }
}
