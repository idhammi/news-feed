package id.idham.newsfeed.feature.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingData
import id.idham.newsfeed.core.domain.GetTopHeadlinesUseCase
import id.idham.newsfeed.core.model.Article
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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

        whenever(getTopHeadlinesUseCase()).thenReturn(flow)

        // When
        viewModel = HomeViewModel(getTopHeadlinesUseCase)

        // Then
        verify(getTopHeadlinesUseCase).invoke()
        assertNotNull(viewModel.articlesFlow)
    }
}
