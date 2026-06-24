package id.idham.newsfeed.core.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        ArticleEntity::class,
        RemoteKeys::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun newsDao(): NewsDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}