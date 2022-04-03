package container.restaurant.android.data.repository

import androidx.annotation.WorkerThread
import com.skydoves.sandwich.*
import container.restaurant.android.data.db.AppDatabase
import container.restaurant.android.data.db.entity.MainFood
import container.restaurant.android.data.db.entity.SideDish
import container.restaurant.android.data.remote.FeedWriteService
import container.restaurant.android.data.remote.LocationService
import container.restaurant.android.data.remote.MyService
import container.restaurant.android.data.request.FeedWriteRequest
import container.restaurant.android.presentation.feed.write.PlaceService
import container.restaurant.android.util.CommUtils
import container.restaurant.android.util.ErrorResponseMapper
import container.restaurant.android.util.flowApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class FeedWriteRepository(
    private val roomDatabase: AppDatabase,
    private val locationService: LocationService,
    private val feedWriteService: FeedWriteService
) {

    private val mainFoodDao by lazy { roomDatabase.mainFoodDao() }
    private val sideDishDao by lazy { roomDatabase.sideDishDao() }

    fun getMainFoodList() = mainFoodDao.getMainFoodList()
    fun getSideDishList() = sideDishDao.getSideDishList()

    @WorkerThread
    suspend fun getSearchLocation(placeName: String) = flow {
        val response = locationService.searchPlace(placeName)
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
    suspend fun postFeed(
        tokenBearer: String,
        feedWriteRequest: FeedWriteRequest
    ) = flowApiResponse(feedWriteService.postFeed(tokenBearer, feedWriteRequest))

//    @WorkerThread
//    fun addMainFood(list: List<MainFood>) = flow {
//        mainFoodDao.updateMainFoodList(list)
//        if(mainFoodDao.getMainFoodCount() < 5) {
//            val mainFood = MainFood(foodName = "",bottle = "")
//            mainFoodDao.insertMainFood(mainFood)
//        }
//        emit("add MainFood Complete")
//    }.flowOn(Dispatchers.IO)
//
//    @WorkerThread
//    fun addSideDish(list: List<SideDish>) = flow {
//        sideDishDao.updateSideDish(list)
//        if(sideDishDao.getSideDishCount() < 5) {
//            val sideDish = SideDish(quantity="", bottle= "")
//            sideDishDao.insertSideDish(sideDish)
//        }
//        emit("add SideDish Complete")
//    }.flowOn(Dispatchers.IO)
//
//    @WorkerThread
//    fun getSearchPlace(place: String, onStart: () -> Unit, onComplete: () -> Unit, onError: (String?) -> Unit) = flow {
//        val response = placeService.getSearchPlace(query = place)
//
//        response.suspendOnSuccess {
//            data?.let { response ->
//                emit(response)
//            }
//        }.onError { map(ErrorResponseMapper) { onError(message) } }
//            .onException { onError(message) }
//    }.onStart { onStart() }.onCompletion { onComplete() }.flowOn(Dispatchers.IO)
//
//    @WorkerThread
//    fun tempLogin(onStart: () -> Unit, onComplete: () -> Unit, onError:(String?) -> Unit) = flow {
//        val response = myService.getTempLogin()
//        response.suspendOnSuccess {
//            data?.let { str ->
//                emit(str)
//            }
//        }.onError { map(ErrorResponseMapper) { onError(message) } }
//            .onException { onError(message) }
//    }.onStart { onStart() }.onCompletion { onComplete() }.flowOn(Dispatchers.IO)
//
//    @WorkerThread
//    fun updateFeed(feedWriteRequest: FeedWriteRequest, onStart: () -> Unit, onComplete: () -> Unit, onError: (String?) -> Unit) = flow {
//        val response = myService.updateFeed(CommUtils.cookieForm, feedWriteRequest)
//        response.suspendOnSuccess {
//            data?.let { resp ->
//                mainFoodDao.initMainFoodData()
//                sideDishDao.initSideDishData()
//                emit("피드 작성 완료")
//            }
//        }.onError { map(ErrorResponseMapper) { onError(message) } }
//            .onException { onError(message) }
//    }.onStart { onStart() }.onCompletion { onComplete() }.flowOn(Dispatchers.IO)
//
//    @WorkerThread
//    fun uploadImg(bmpFile: File, onStart: () -> Unit, onComplete: () -> Unit, onError: (String?) -> Unit) = flow {
//
//        val filePart = MultipartBody.Part.createFormData("image",bmpFile.name,bmpFile.asRequestBody("multipart/form-data".toMediaTypeOrNull()))
//
//        val response = myService.uploadImage(filePart)
//        response.suspendOnSuccess {
//            data?.let { response ->
//                emit(response)
//            }
//        }.onError { map(ErrorResponseMapper) { onError(message) } }
//            .onException { onError(message) }
//    }.onStart { onStart() }.onCompletion { onComplete() }.flowOn(Dispatchers.IO)
}