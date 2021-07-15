package container.restaurant.android.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skydoves.sandwich.ApiResponse
import container.restaurant.android.data.PrefStorage
import container.restaurant.android.data.repository.HomeRepository
import container.restaurant.android.data.response.BannersInfoResponse
import container.restaurant.android.data.response.SignInWithAccessTokenResponse
import container.restaurant.android.util.Event
import container.restaurant.android.util.handleApiResponse
import kotlinx.coroutines.flow.collect
import timber.log.Timber

internal class HomeViewModel(
    private val prefStorage: PrefStorage,
    private val homeRepository: HomeRepository
) : ViewModel() {

    val navToAllContainerFeed = MutableLiveData<Event<Unit>>()

    private val _signInWithAccessTokenResult = MutableLiveData<SignInWithAccessTokenResponse>()
    val signInWithAccessTokenResult: LiveData<SignInWithAccessTokenResponse> = _signInWithAccessTokenResult

    private val _bannerList = MutableLiveData<BannersInfoResponse.BannerInfoDtoList>()
    val bannerList: LiveData<BannersInfoResponse.BannerInfoDtoList> = _bannerList

    private val _isNavToAllContainerFeedClicked = MutableLiveData<Event<Boolean>>()
    val isNavToAllContainerFeedClicked: LiveData<Event<Boolean>> = _isNavToAllContainerFeedClicked

    private val _isNavToMyContainerFeedClicked = MutableLiveData<Event<Boolean>>()
    val isNavToMyContainerFeedClicked: LiveData<Event<Boolean>> = _isNavToMyContainerFeedClicked

    fun onClickMyContainerFeed() {
        _isNavToMyContainerFeedClicked.value = Event(true)
    }

    fun onClickAllContainerFeed() {
        _isNavToAllContainerFeedClicked.value = Event(true)
    }

    fun isUserSignIn(): Boolean {
        return prefStorage.isUserSignIn
    }

    suspend fun getBannersInfo() {
        homeRepository.getBannersInfo()
            .collect { response ->
                handleApiResponse(
                    response = response,
                    onSuccess = {
                        _bannerList.value = it.data?.embedded
                        Timber.d("it.data : ${it.data}")
                        Timber.d("it.headers : ${it.headers}")
                        Timber.d("it.raw : ${it.raw}")
                        Timber.d("it.response : ${it.response}")
                        Timber.d("it.statusCode : ${it.statusCode}")
                    },
                    onError = {
                        Timber.d("it.errorBody : ${it.errorBody}")
                        Timber.d("it.headers : ${it.headers}")
                        Timber.d("it.raw : ${it.raw}")
                        Timber.d("it.response : ${it.response}")
                        Timber.d("it.statusCode : ${it.statusCode}")
                    },
                    onException = {
                        Timber.d("it.message : ${it.message}")
                        Timber.d("it.exception : ${it.exception}")
                    },
                )
            }
    }


}