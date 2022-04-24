package container.restaurant.android.data.repository

import androidx.annotation.WorkerThread
import com.skydoves.sandwich.suspendOnError
import com.skydoves.sandwich.suspendOnException
import com.skydoves.sandwich.suspendOnSuccess
import container.restaurant.android.data.remote.FeedDetailService
import container.restaurant.android.data.remote.FeedExploreService
import container.restaurant.android.util.flowApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class FeedDetailRepository(
    private val feedDetailService: FeedDetailService,
    private val feedExploreService: FeedExploreService
) {
    @WorkerThread
    suspend fun getFeedDetail(tokenBearer: String, feedId: Int) = flow{
        val response = feedDetailService.feedDetail(tokenBearer, feedId)
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

    @WorkerThread
    suspend fun scrapFeed(tokenBearer: String?, feedId: Int) =
        flowApiResponse(feedDetailService.scrapFeed(tokenBearer, feedId))

    @WorkerThread
    suspend fun cancelScrapFeed(tokenBearer: String?, feedId: Int) =
        flowApiResponse(feedDetailService.cancelScrapFeed(tokenBearer, feedId))
}