package id.idham.newsfeed.core.data.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import id.idham.newsfeed.core.data.exception.GenericException
import id.idham.newsfeed.core.data.exception.NetworkException
import id.idham.newsfeed.core.data.exception.NotFoundException
import id.idham.newsfeed.core.data.exception.RateLimitException
import id.idham.newsfeed.core.data.exception.UnauthorizedException
import id.idham.newsfeed.core.model.Article
import id.idham.newsfeed.core.network.endpoint.NewsApiService
import id.idham.newsfeed.core.network.model.asExternalModel
import retrofit2.HttpException
import java.io.IOException

class NewsPagingSource(
    private val api: NewsApiService,
    private val category: String,
) : PagingSource<Int, Article>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        return try {
            val page = params.key ?: STARTING_PAGE
            val pageSize = params.loadSize

            val response = api.getTopHeadlines(
                category = category,
                pageSize = pageSize.toString(),
                page = page.toString()
            )

            val articles = response.asExternalModel().articles

            LoadResult.Page(
                data = articles,
                prevKey = if (page == STARTING_PAGE) null else page - 1,
                nextKey = if (articles.isEmpty()) null else page + 1
            )
        } catch (e: HttpException) {
            LoadResult.Error(mapHttpException(e))
        } catch (e: IOException) {
            LoadResult.Error(NetworkException("Network error. Please check your connection."))
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private fun mapHttpException(e: HttpException): Exception {
        return when (e.code()) {
            401 -> UnauthorizedException("Invalid API key. Please check your NewsAPI key configuration.")
            429 -> RateLimitException("API rate limit exceeded. Please try again later.")
            404 -> NotFoundException("The requested resource was not found.")
            else -> GenericException("HTTP ${e.code()}: ${e.message()}")
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    companion object {
        private const val STARTING_PAGE = 1
    }
}