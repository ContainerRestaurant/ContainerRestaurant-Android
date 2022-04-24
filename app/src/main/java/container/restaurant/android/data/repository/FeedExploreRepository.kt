package container.restaurant.android.data.repository

import androidx.annotation.WorkerThread
import com.skydoves.sandwich.suspendOnError
import com.skydoves.sandwich.suspendOnException
import com.skydoves.sandwich.suspendOnSuccess
import com.tak8997.github.domain.ResultState
import container.restaurant.android.data.SortingCategory
import container.restaurant.android.data.remote.FeedExploreService
import container.restaurant.android.data.response.FeedResponse
import container.restaurant.android.data.safeApiCall
import container.restaurant.android.data.FeedCategory
import container.restaurant.android.util.flowApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.http.Header

private const val perPage = 20

class FeedExploreRepository(
    private val feedExploreService: FeedExploreService
) {

    private var feedCategory: String? = null

    suspend fun fetchFeedsWithCategory(
        category: String,
        sortingCategory: SortingCategory,
        page: Int
    ): ResultState<FeedResponse> {
        feedCategory = category
        if (category == FeedCategory.ALL.name) {
            feedCategory = null
        }

        return safeApiCall {
            feedExploreService.fetchFeeds(
                feedCategory,
                sortingCategory.sort,
                page,
                perPage
            )
        }
    }

    suspend fun fetchResFeed(resId: Long): ResultState<FeedResponse> {
        return safeApiCall { feedExploreService.fetchResFeed(resId) }
    }

    @WorkerThread
    suspend fun getFeedList(tokenBearer: String, categoryName: String?, sortingCategory: SortingCategory, page: Int) = flow {
        val response = feedExploreService.feedList(tokenBearer, categoryName, sortingCategory.sort, page, perPage)
        response
            .suspendOnSuccess {
                emit(this)
            }
            .suspendOnError {
                emit(this)
            }
            .suspendOnException {
                emit(this)
            }
    }.flowOn(Dispatchers.IO)

    @WorkerThread
    suspend fun likeFeed(tokenBearer: String, feedId: Int) =
        flowApiResponse(feedExploreService.likeFeed(tokenBearer, feedId))

    @WorkerThread
    suspend fun cancelLikeFeed(tokenBearer: String, feedId: Int) =
        flowApiResponse(feedExploreService.cancelLikeFeed(tokenBearer, feedId))
}
