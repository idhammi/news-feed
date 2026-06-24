package id.idham.newsfeed.core.data.source

import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator
import id.idham.newsfeed.core.database.AppDatabase
import id.idham.newsfeed.core.network.endpoint.NewsApiService
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalPagingApi::class)
class NewsRemoteMediatorTest {

    @Mock
    private lateinit var api: NewsApiService

    @Mock
    private lateinit var db: AppDatabase

    private lateinit var mediator: NewsRemoteMediator

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        mediator = NewsRemoteMediator(
            api = api,
            db = db,
            category = "general"
        )
    }

    @Test
    fun `initialize returns LAUNCH_INITIAL_REFRESH`() = runTest {
        val result = mediator.initialize()
        assertEquals(RemoteMediator.InitializeAction.LAUNCH_INITIAL_REFRESH, result)
    }
}
