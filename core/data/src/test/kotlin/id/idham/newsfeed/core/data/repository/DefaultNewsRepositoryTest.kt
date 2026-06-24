package id.idham.newsfeed.core.data.repository

import androidx.paging.PagingSource
import id.idham.newsfeed.core.database.AppDatabase
import id.idham.newsfeed.core.database.ArticleEntity
import id.idham.newsfeed.core.database.NewsDao
import id.idham.newsfeed.core.network.endpoint.NewsApiService
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class DefaultNewsRepositoryTest {

    @Mock
    private lateinit var api: NewsApiService

    @Mock
    private lateinit var db: AppDatabase

    @Mock
    private lateinit var newsDao: NewsDao

    @Mock
    private lateinit var pagingSource: PagingSource<Int, ArticleEntity>

    private lateinit var repository: NewsRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        `when`(db.newsDao()).thenReturn(newsDao)
        repository = DefaultNewsRepository(api, db)
    }

    @Test
    fun `getTopHeadlines returns non-null paging data flow`() = runTest {
        // Given
        val category = "general"
        `when`(newsDao.pagingSource(category)).thenReturn(pagingSource)

        // When
        val flow = repository.getTopHeadlines(category)

        // Then
        assertNotNull(flow)
    }

    @Test
    fun `getTopHeadlines accepts different categories`() = runTest {
        // Given
        val category = "technology"
        `when`(newsDao.pagingSource(category)).thenReturn(pagingSource)

        // When
        val flow = repository.getTopHeadlines(category)

        // Then
        assertNotNull(flow)
    }
}
