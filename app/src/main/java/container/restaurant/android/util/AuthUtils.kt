package container.restaurant.android.util

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenCreated
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import container.restaurant.android.data.response.UserInfoResponse
import container.restaurant.android.presentation.auth.AuthViewModel
import container.restaurant.android.presentation.auth.KakaoSignInDialogFragment
import container.restaurant.android.presentation.auth.SignUpActivity
import timber.log.Timber
import java.util.regex.Pattern

class ValidationCheck {
    companion object {
        const val MIN_LENGTH = 2
        const val MAX_LENGTH = 20
        val impossibleLetterPattern: Pattern = Pattern.compile("[^가-힣a-zA-Z\\d ]")
        val koreanPattern: Pattern = Pattern.compile("[가-힣]")
        val enNumSpacePattern: Pattern = Pattern.compile("[a-zA-Z\\d ]")
    }
}

fun letterValidationCheck(string: String): Boolean {
    val matcher = ValidationCheck.impossibleLetterPattern.matcher(string)
    if (matcher.find()) return false
    return true
}

fun lengthLongValidationCheck(string: String): Boolean {
    var length = 0
    val koreanMatcher = ValidationCheck.koreanPattern.matcher(string)
    while (koreanMatcher.find()) {
        length++
        if (length > ValidationCheck.MAX_LENGTH) return false
    }

    val enNumSpaceMatcher = ValidationCheck.enNumSpacePattern.matcher(string)
    while (enNumSpaceMatcher.find()) {
        length++
        if (length > ValidationCheck.MAX_LENGTH) return false
    }
    return true
}

fun lengthShortValidationCheck(string: String): Boolean {
    var length = 0
    val koreanMatcher = ValidationCheck.koreanPattern.matcher(string)
    while (koreanMatcher.find()) {
        length += 2
        if (length >= ValidationCheck.MIN_LENGTH) return true
    }

    val enNumSpaceMatcher = ValidationCheck.enNumSpacePattern.matcher(string)
    while (enNumSpaceMatcher.find()) {
        length++
        if (length >= ValidationCheck.MIN_LENGTH) return true
    }
    return false
}

/**
 * 이미 프로젝트에 저장된 토큰, 아이디가 있을 때
 **/
suspend fun ifAlreadySignIn(
    authViewModel: AuthViewModel,
    fragmentActivity: FragmentActivity
) {
    authViewModel.signInWithAccessToken(
        onInvalidToken = {
            kakaoLogin(fragmentActivity) { token, err ->
                if (err != null) {
                    Timber.e(err, "카카오 인증 실패")
                    Toast.makeText(fragmentActivity, "카카오 인증 실패", Toast.LENGTH_LONG).show()
                } else if (token != null) {
                    UserApiClient.instance.me { userKakao, err2 ->
                        if (err2 != null) {
                            Timber.e(err2, "카카오 사용자 정보 요청 실패")
                            Toast.makeText(
                                fragmentActivity,
                                "카카오 사용자 정보 요청 실패",
                                Toast.LENGTH_LONG
                            ).show()
                        } else if (userKakao != null) {
                            Timber.d("카카오 인증 성공")
                            val provider = Provider.KAKAO.providerStr
                            val kakaoAccessToken = token.accessToken
                            fragmentActivity.lifecycleScope.launchWhenCreated {
                                authViewModel.generateAccessToken(
                                    provider,
                                    kakaoAccessToken
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

fun observeAuthViewModelUserInfo(lifecycleOwner: LifecycleOwner, authViewModel: AuthViewModel, onSignInSuccess: (UserInfoResponse) -> Unit) {
    with(authViewModel) {
        userInfoResponse.observe(lifecycleOwner, object : Observer<UserInfoResponse> {
            override fun onChanged(it: UserInfoResponse?) {
                if(it!=null){
                    onSignInSuccess(it)
                    // 옵저빙 뒤에는 현재 옵저버를 제거해줌
                    userInfoResponse.removeObserver(this)
                }
            }
        })
    }
}

fun kakaoLogin(context: Context, callback:(OAuthToken?, Throwable?) -> Unit) {
    val kakaoUserApi = UserApiClient.instance
    if (kakaoUserApi.isKakaoTalkLoginAvailable(context)) {
        kakaoUserApi.loginWithKakaoTalk(context, callback = callback)
    } else {
        kakaoUserApi.loginWithKakaoAccount(context, callback = callback)
    }
}