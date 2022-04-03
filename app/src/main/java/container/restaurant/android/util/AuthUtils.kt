package container.restaurant.android.util

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import container.restaurant.android.data.response.UserInfoResponse
import container.restaurant.android.presentation.auth.AuthViewModel
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
    fragmentActivity: FragmentActivity,
    onSignInSuccess: (UserInfoResponse?) -> Unit,
) {
    val tokenBearer = SharedPrefUtil.getString(fragmentActivity) { TOKEN_BEARER }
    authViewModel.signInWithAccessToken(
        tokenBearer,
        onSignInSuccess = onSignInSuccess,
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
                                    kakaoAccessToken,
                                    onGenerateSuccess = { token, userId ->
                                        SharedPrefUtil.setBoolean(fragmentActivity, { IS_USER_LOGIN }, true)
                                        if(token != null) SharedPrefUtil.setString(fragmentActivity, { TOKEN_BEARER }, "Bearer $token")
                                        if(userId != null) SharedPrefUtil.setInt(fragmentActivity, { USER_ID }, userId)
                                        onSignInSuccess(null)
                                    }, onGenerateFail = {
                                        Toast.makeText(fragmentActivity, "회원가입 혹은 로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

fun kakaoLogin(context: Context, callback:(OAuthToken?, Throwable?) -> Unit) {
    val kakaoUserApi = UserApiClient.instance
    if (kakaoUserApi.isKakaoTalkLoginAvailable(context)) {
        kakaoUserApi.loginWithKakaoTalk(context, callback = callback)
    } else {
        kakaoUserApi.loginWithKakaoAccount(context, callback = callback)
    }
}

fun logOut(context: Context) {
    SharedPrefUtil.setBoolean(context, { IS_USER_LOGIN }, false)
    SharedPrefUtil.setInt(context, { USER_ID }, SharedPrefManager.DEFAULT_VALUE_INT)
    SharedPrefUtil.setString(context, { TOKEN_BEARER }, SharedPrefManager.DEFAULT_VALUE_STRING)
}