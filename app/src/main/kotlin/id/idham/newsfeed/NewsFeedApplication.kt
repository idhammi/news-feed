package id.idham.newsfeed

import android.app.Application
import id.idham.newsfeed.core.data.di.dataModule
import id.idham.newsfeed.core.domain.di.domainModule
import id.idham.newsfeed.core.network.di.newsNetworkModule
import id.idham.newsfeed.core.network.di.networkModule
import id.idham.newsfeed.core.database.databaseModule
import id.idham.newsfeed.core.database.daoModule
import id.idham.newsfeed.feature.home.di.homeModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class NewsFeedApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@NewsFeedApplication)
            modules(
                networkModule,
                newsNetworkModule,
                databaseModule,
                daoModule,
                dataModule,
                domainModule,
                homeModule
            )
        }
    }
}
