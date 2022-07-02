package container.restaurant.android.data.repository

import androidx.annotation.WorkerThread
import com.skydoves.sandwich.*
import container.restaurant.android.data.remote.MyService
import container.restaurant.android.util.flowApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MyRepository(private val myService: MyService) {

    @WorkerThread
    suspend fun getUserInfo(tokenBearer: String) = flow {
        val response = myService.myInfo(tokenBearer)
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
    suspend fun getContract() = flow {
        val response = myService.contract()
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
    suspend fun getMyFeed(tokenBearer: String, userId: Int) = flow {
        val response = myService.myFeed(tokenBearer, userId)
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
    suspend fun getMyScrapFeed(tokenBearer: String, userId: Int) = flow {
        val response = myService.myScrapFeed(tokenBearer, userId)
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
    suspend fun getFavoriteRestaurant(tokenBearer: String) = flowApiResponse(myService.getFavoriteRestaurant(tokenBearer))

    @WorkerThread
    suspend fun likeFeed(tokenBearer: String, feedId: Int) =
        flowApiResponse(myService.likeFeed(tokenBearer, feedId))

    @WorkerThread
    suspend fun cancelLikeFeed(tokenBearer: String, feedId: Int) =
        flowApiResponse(myService.cancelLikeFeed(tokenBearer, feedId))
}