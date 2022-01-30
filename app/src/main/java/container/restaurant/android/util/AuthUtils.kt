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

/**
 * 이미 프로젝트에 저장된 토큰, 아이디가 없을 때
 **/
fun observeKakaoFragmentData(
    fragmentActivity: FragmentActivity,
    kakaoSignInDialogFragment: KakaoSignInDialogFragment,
    signUpResultLauncher: ActivityResultLauncher<Intent>,
    onSignInSuccess: (UserInfoResponse) -> Unit
) {
    fragmentActivity.lifecycleScope.launchWhenCreated {
        kakaoSignInDialogFragment.whenCreated {
            with(kakaoSignInDialogFragment.viewModel) {
                isGenerateAccessTokenSuccess.observe(fragmentActivity, object: Observer<Void> {
                    override fun onChanged(t: Void?) {
                        fragmentActivity.lifecycleScope.launchWhenCreated {
                            signInWithAccessToken(
                                onNicknameNull = {
                                    signUpResultLauncher.launch(
                                        Intent(
                                            fragmentActivity,
                                            SignUpActivity::class.java
                                        ))
                                    kakaoSignInDialogFragment.dismiss()
                                },
                                onSignInSuccess = {
                                    onSignInSuccess(it)
                                    kakaoSignInDialogFragment.dismiss()
                                }
                            )
                        }
                        // 옵저빙 뒤에는 현재 옵저버를 제거해줌
                        isGenerateAccessTokenSuccess.removeObserver(this)
                    }
                })
            }
        }
    }
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