package id.idham.newsfeed.core.data.repository

import id.idham.newsfeed.core.network.endpoint.NewsApiService
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class DefaultNewsRepositoryTest {

    @Mock
    private lateinit var api: NewsApiService

    private lateinit var repository: NewsRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = DefaultNewsRepository(api)
    }

    @Test
    fun `getTopHeadlines returns non-null paging data flow`() = runTest {
        // Given
        val category = "general"

        // When
        val flow = repository.getTopHeadlines(category)

        // Then
        assertNotNull(flow)
    }

    @Test
    fun `getTopHeadlines accepts different categories`() = runTest {
        // Given
        val category = "technology"

        // When
        val flow = repository.getTopHeadlines(category)

        // Then
        assertNotNull(flow)
    }
}
