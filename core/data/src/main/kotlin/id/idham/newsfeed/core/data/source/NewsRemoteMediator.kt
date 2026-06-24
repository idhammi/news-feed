package id.idham.newsfeed.core.data.source

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import id.idham.newsfeed.core.data.exception.GenericException
import id.idham.newsfeed.core.data.exception.NetworkException
import id.idham.newsfeed.core.data.exception.NotFoundException
import id.idham.newsfeed.core.data.exception.RateLimitException
import id.idham.newsfeed.core.data.exception.UnauthorizedException
import id.idham.newsfeed.core.database.AppDatabase
import id.idham.newsfeed.core.database.ArticleEntity
import id.idham.newsfeed.core.database.RemoteKeys
import id.idham.newsfeed.core.database.toEntity
import id.idham.newsfeed.core.network.endpoint.NewsApiService
import id.idham.newsfeed.core.network.model.asExternalModel
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class NewsRemoteMediator(
    private val db: AppDatabase,
    private val api: NewsApiService,
    private val category: String,
    private val country: String? = null
) : RemoteMediator<Int, ArticleEntity>() {

    private val cacheCategory = if (country != null) "${category}_$country" else category

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ArticleEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: STARTING_PAGE
            }

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                prevKey
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                nextKey
            }
        }

        return try {
            val response = api.getTopHeadlines(
                category = category,
                country = country,
                pageSize = state.config.pageSize.toString(),
                page = page.toString()
            )

            val articles = response.asExternalModel().articles
            val endOfPaginationReached = articles.isEmpty()

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.remoteKeysDao().clearRemoteKeys()
                    db.newsDao().clearAll(cacheCategory)
                }

                val prevKey = if (page == STARTING_PAGE) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1

                val keys = articles.map {
                    RemoteKeys(articleUrl = it.url, prevKey = prevKey, nextKey = nextKey)
                }

                db.remoteKeysDao().insertAll(keys)
                db.newsDao().insertAll(articles.toEntity(cacheCategory))
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: HttpException) {
            MediatorResult.Error(mapHttpException(e))
        } catch (e: IOException) {
            MediatorResult.Error(NetworkException("Network error. Please check your connection."))
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ArticleEntity>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { article ->
                db.remoteKeysDao().remoteKeysArticleId(article.url)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ArticleEntity>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { article ->
                db.remoteKeysDao().remoteKeysArticleId(article.url)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, ArticleEntity>
    ): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.url?.let { url ->
                db.remoteKeysDao().remoteKeysArticleId(url)
            }
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

    companion object {
        private const val STARTING_PAGE = 1
    }
}
