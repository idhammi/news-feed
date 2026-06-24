package id.idham.newsfeed.feature.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingData
import id.idham.newsfeed.core.data.location.LocationTracker
import id.idham.newsfeed.core.domain.GetTopHeadlinesUseCase
import id.idham.newsfeed.core.model.Article
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var getTopHeadlinesUseCase: GetTopHeadlinesUseCase

    @Mock
    private lateinit var locationTracker: LocationTracker

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `viewModel initializes articlesFlow from use case`() = runTest {
        // Given
        val pagingData = PagingData.from(emptyList<Article>())
        val flow = flowOf(pagingData)

        whenever(getTopHeadlinesUseCase(null)).thenReturn(flow)

        // When
        viewModel = HomeViewModel(getTopHeadlinesUseCase, locationTracker)

        // Trigger collection
        val job = launch(testDispatcher) {
            viewModel.articlesFlow.collect {}
        }
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify(getTopHeadlinesUseCase).invoke(null)
        assertNotNull(viewModel.articlesFlow)
        job.cancel()
    }

    @Test
    fun `fetchLocalNews updates country from locationTracker`() = runTest {
        // Given
        val pagingData = PagingData.from(emptyList<Article>())
        val flow = flowOf(pagingData)
        whenever(getTopHeadlinesUseCase(null)).thenReturn(flow)
        whenever(getTopHeadlinesUseCase("id")).thenReturn(flow)
        whenever(locationTracker.getCurrentCountryCode()).thenReturn("id")

        viewModel = HomeViewModel(getTopHeadlinesUseCase, locationTracker)
        val job = launch(testDispatcher) { viewModel.articlesFlow.collect {} }

        // When
        viewModel.fetchLocalNews()
        testDispatcher.scheduler.advanceUntilIdle() // Ensure coroutines run

        // Then
        verify(locationTracker).getCurrentCountryCode()
        verify(getTopHeadlinesUseCase).invoke("id")
        job.cancel()
    }

    @Test
    fun `fetchGlobalNews resets country to null`() = runTest {
        // Given
        val pagingData = PagingData.from(emptyList<Article>())
        val flow = flowOf(pagingData)
        whenever(getTopHeadlinesUseCase(null)).thenReturn(flow)
        whenever(getTopHeadlinesUseCase("us")).thenReturn(flow)
        whenever(locationTracker.getCurrentCountryCode()).thenReturn("us")

        viewModel = HomeViewModel(getTopHeadlinesUseCase, locationTracker)
        val job = launch(testDispatcher) { viewModel.articlesFlow.collect {} }

        viewModel.fetchLocalNews()
        testDispatcher.scheduler.advanceUntilIdle()
        verify(getTopHeadlinesUseCase).invoke("us")

        // When
        viewModel.fetchGlobalNews()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify(getTopHeadlinesUseCase).invoke(null) // Should be called with null again
        job.cancel()
    }
}
