package id.idham.newsfeed.core.database

import androidx.room.Room
import org.koin.dsl.module

val databaseModule = module {
    single<AppDatabase> {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            "database-name"
        )
            .build()

    }
}

val daoModule = module {
    single<NewsDao> {
        get<AppDatabase>().newsDao()
    }
    single<RemoteKeysDao> {
        get<AppDatabase>().remoteKeysDao()
    }
}