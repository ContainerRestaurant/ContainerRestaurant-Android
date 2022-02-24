package container.restaurant.android.presentation.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skydoves.sandwich.StatusCode
import container.restaurant.android.R
import container.restaurant.android.data.repository.AuthRepository
import container.restaurant.android.data.response.UserInfoResponse
import container.restaurant.android.presentation.base.BaseViewModel
import container.restaurant.android.util.Event
import container.restaurant.android.util.SingleLiveEvent
import container.restaurant.android.util.handleApiResponse
import kotlinx.coroutines.flow.collect

class KakaoSignInDialogViewModel(private val authRepository: AuthRepository): BaseViewModel() {

    val userInfoResponse = MutableLiveData<UserInfoResponse>()

    private val _tokenBearer = SingleLiveEvent<String>()
    val tokenBearer: LiveData<String> = _tokenBearer

    private val _errorMessageId = MutableLiveData<Event<Int>>()
    val errorMessageId: LiveData<Event<Int>> = _errorMessageId

    suspend fun generateAccessToken(provider: String, accessToken: String, onGenerateSuccess: () -> Unit, onGenerateFail: () -> Unit) {
        authRepository.generateAccessToken(
            provider = provider,
            accessToken = accessToken
        ).collect { response ->
            handleApiResponse(
                response = response,
                onSuccess = {

                },
                onError = {
                    when (it.statusCode) {
                        StatusCode.BadRequest -> {
                            // 올바르지 않은 닉네임
                        }
                        else -> {
                            _errorMessageId.value = Event(R.string.error_message_other)
                        }
                    }
                }, onException = {
                    _errorMessageId.value = Event(R.string.error_message_other)
                }
            )
        }
    }

    suspend fun signInWithAccessToken(
        tokenBearer: String,
        onNicknameNull: () -> Unit = {},
        onSignInSuccess: (UserInfoResponse) -> Unit = {},
        onInvalidToken: () -> Unit = {}
    ) {
        authRepository.signInWithAccessToken(tokenBearer)
            .collect { response ->
                handleApiResponse(response = response,
                    onSuccess = {
                        if (it.data?.nickname == null) {
                            onNicknameNull()
                        } else {
                            it.data?.let { userInfoResponse ->
                                onSignInSuccess(userInfoResponse)
                                this.userInfoResponse.value = it.data
                            }
                        }
                    },
                    onError = {
                        if (it.statusCode == StatusCode.Unauthorized) {
                            onInvalidToken()
                        }
                    }
                )
            }
    }
}