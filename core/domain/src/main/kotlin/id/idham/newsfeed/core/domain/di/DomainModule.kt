package id.idham.newsfeed.core.domain.di

import id.idham.newsfeed.core.domain.GetTopHeadlinesUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
    factoryOf(::GetTopHeadlinesUseCase)
}
