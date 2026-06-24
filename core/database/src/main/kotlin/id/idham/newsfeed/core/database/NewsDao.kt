package id.idham.newsfeed.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(news: List<ArticleEntity>)

    @Query("SELECT * FROM article WHERE category = :category")
    fun pagingSource(category: String): androidx.paging.PagingSource<Int, ArticleEntity>

    @Query("DELETE FROM article WHERE category = :category")
    suspend fun clearAll(category: String)
}
