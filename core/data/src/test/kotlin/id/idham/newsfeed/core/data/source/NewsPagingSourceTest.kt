package id.idham.newsfeed.core.data.source

import androidx.paging.PagingSource
import id.idham.newsfeed.core.data.exception.NetworkException
import id.idham.newsfeed.core.data.exception.NotFoundException
import id.idham.newsfeed.core.data.exception.RateLimitException
import id.idham.newsfeed.core.data.exception.UnauthorizedException
import id.idham.newsfeed.core.network.endpoint.NewsApiService
import id.idham.newsfeed.core.network.model.ArticleResponse
import id.idham.newsfeed.core.network.model.NewsResponse
import id.idham.newsfeed.core.network.model.SourceResponse
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class NewsPagingSourceTest {

    @Mock
    private lateinit var api: NewsApiService

    private lateinit var pagingSource: NewsPagingSource

    private val category = "general"

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        pagingSource = NewsPagingSource(api, category)
    }

    @Test
    fun `load returns page when successful`() = runTest {
        // Given
        val articles = createArticleResponseList()
        val newsResponse = NewsResponse(
            status = "ok",
            totalResults = 20,
            articles = articles
        )

        whenever(
            api.getTopHeadlines(
                category = category,
                pageSize = "20",
                page = "1"
            )
        ).thenReturn(newsResponse)

        // When
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        // Then
        assertTrue(result is PagingSource.LoadResult.Page)
        val pageResult = result as PagingSource.LoadResult.Page
        assertEquals(2, pageResult.data.size)
        assertNull(pageResult.prevKey)
        assertEquals(2, pageResult.nextKey)
    }

    @Test
    fun `load returns page with correct prev and next keys for page 2`() = runTest {
        // Given
        val articles = createArticleResponseList()
        val newsResponse = NewsResponse(
            status = "ok",
            totalResults = 40,
            articles = articles
        )

        whenever(
            api.getTopHeadlines(
                category = category,
                pageSize = "20",
                page = "2"
            )
        ).thenReturn(newsResponse)

        // When
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 2,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        // Then
        assertTrue(result is PagingSource.LoadResult.Page)
        val pageResult = result as PagingSource.LoadResult.Page
        assertEquals(2, pageResult.data.size)
        assertEquals(1, pageResult.prevKey)
        assertEquals(3, pageResult.nextKey)
    }

    @Test
    fun `load returns page with null next key when articles are empty`() = runTest {
        // Given
        val newsResponse = NewsResponse(
            status = "ok",
            totalResults = 0,
            articles = emptyList()
        )

        whenever(
            api.getTopHeadlines(
                category = category,
                pageSize = "20",
                page = "1"
            )
        ).thenReturn(newsResponse)

        // When
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        // Then
        assertTrue(result is PagingSource.LoadResult.Page)
        val pageResult = result as PagingSource.LoadResult.Page
        assertEquals(0, pageResult.data.size)
        assertNull(pageResult.prevKey)
        assertNull(pageResult.nextKey)
    }

    @Test
    fun `load returns error when 401 unauthorized`() = runTest {
        // Given
        val httpException = HttpException(
            Response.error<NewsResponse>(
                401,
                "Unauthorized".toResponseBody("text/plain".toMediaTypeOrNull())
            )
        )

        whenever(
            api.getTopHeadlines(
                category = category,
                pageSize = "20",
                page = "1"
            )
        ).thenThrow(httpException)

        // When
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        // Then
        assertTrue(result is PagingSource.LoadResult.Error)
        val error = (result as PagingSource.LoadResult.Error).throwable
        assertTrue(error is UnauthorizedException)
        assertEquals("Invalid API key. Please check your NewsAPI key configuration.", error.message)
    }

    @Test
    fun `load returns error when 429 rate limit exceeded`() = runTest {
        // Given
        val httpException = HttpException(
            Response.error<NewsResponse>(
                429,
                "Rate limit exceeded".toResponseBody("text/plain".toMediaTypeOrNull())
            )
        )

        whenever(
            api.getTopHeadlines(
                category = category,
                pageSize = "20",
                page = "1"
            )
        ).thenThrow(httpException)

        // When
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        // Then
        assertTrue(result is PagingSource.LoadResult.Error)
        val error = (result as PagingSource.LoadResult.Error).throwable
        assertTrue(error is RateLimitException)
        assertEquals("API rate limit exceeded. Please try again later.", error.message)
    }

    @Test
    fun `load returns error when 404 not found`() = runTest {
        // Given
        val httpException = HttpException(
            Response.error<NewsResponse>(
                404,
                "Not found".toResponseBody("text/plain".toMediaTypeOrNull())
            )
        )

        whenever(
            api.getTopHeadlines(
                category = category,
                pageSize = "20",
                page = "1"
            )
        ).thenThrow(httpException)

        // When
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        // Then
        assertTrue(result is PagingSource.LoadResult.Error)
        val error = (result as PagingSource.LoadResult.Error).throwable
        assertTrue(error is NotFoundException)
        assertEquals("The requested resource was not found.", error.message)
    }

    @Test
    fun `load returns error when IOException occurs`() = runTest {
        // Given
        whenever(
            api.getTopHeadlines(
                category = category,
                pageSize = "20",
                page = "1"
            )
        ).thenAnswer { throw IOException("Network error") }

        // When
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        // Then
        assertTrue(result is PagingSource.LoadResult.Error)
        val error = (result as PagingSource.LoadResult.Error).throwable
        assertTrue(error is NetworkException)
        assertEquals("Network error. Please check your connection.", error.message)
    }

    @Test
    fun `load handles null articles list`() = runTest {
        // Given
        val newsResponse = NewsResponse(
            status = "ok",
            totalResults = 0,
            articles = null
        )

        whenever(
            api.getTopHeadlines(
                category = category,
                pageSize = "20",
                page = "1"
            )
        ).thenReturn(newsResponse)

        // When
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        // Then
        assertTrue(result is PagingSource.LoadResult.Page)
        val pageResult = result as PagingSource.LoadResult.Page
        assertEquals(0, pageResult.data.size)
    }

    @Test
    fun `load handles articles with null fields`() = runTest {
        // Given
        val articles = listOf(
            ArticleResponse(
                source = null,
                author = null,
                title = null,
                description = null,
                url = null,
                urlToImage = null,
                publishedAt = null,
                content = null
            )
        )
        val newsResponse = NewsResponse(
            status = "ok",
            totalResults = 1,
            articles = articles
        )

        whenever(
            api.getTopHeadlines(
                category = category,
                pageSize = "20",
                page = "1"
            )
        ).thenReturn(newsResponse)

        // When
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        // Then
        assertTrue(result is PagingSource.LoadResult.Page)
        val pageResult = result as PagingSource.LoadResult.Page
        assertEquals(1, pageResult.data.size)
        val article = pageResult.data[0]
        assertEquals("", article.title)
        assertEquals("", article.description)
        assertEquals("", article.author)
    }

    private fun createArticleResponseList(): List<ArticleResponse> {
        return listOf(
            ArticleResponse(
                source = SourceResponse(id = "1", name = "TechCrunch"),
                author = "John Doe",
                title = "Breaking News 1",
                description = "Description 1",
                url = "https://example.com/1",
                urlToImage = "https://example.com/image1.jpg",
                publishedAt = "2024-01-15T10:30:00Z",
                content = "Content 1"
            ),
            ArticleResponse(
                source = SourceResponse(id = "2", name = "BBC News"),
                author = "Jane Smith",
                title = "Breaking News 2",
                description = "Description 2",
                url = "https://example.com/2",
                urlToImage = "https://example.com/image2.jpg",
                publishedAt = "2024-01-15T11:30:00Z",
                content = "Content 2"
            )
        )
    }
}
