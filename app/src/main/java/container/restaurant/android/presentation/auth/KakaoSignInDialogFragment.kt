package container.restaurant.android.presentation.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import container.restaurant.android.R
import container.restaurant.android.databinding.FragmentKakaoSigninBinding
import timber.log.Timber


internal class KakaoSignInDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentKakaoSigninBinding

    private val kakaoUserApi by lazy {
        UserApiClient.instance
    }

    private val callback: (OAuthToken?, Throwable?) -> Unit = { token, err ->
        if (err != null) {
            Timber.e(err,"카카오 인증 실패")
        } else if (token != null) {
            kakaoUserApi.me { userKakao, err2 ->
                if(err2 != null) {
                    Timber.e(err2, "카카오 사용자 정보 요청 실패")
                } else if(userKakao != null){
                    Timber.d("카카오 인증 성공")
                    startActivity(SignInActivity.getIntent(requireContext()))
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentKakaoSigninBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvKakaoLogin.setOnClickListener {
            if (kakaoUserApi.isKakaoTalkLoginAvailable(requireContext())) {
                kakaoUserApi.loginWithKakaoTalk(requireContext(), callback = callback)
            } else {
                kakaoUserApi.loginWithKakaoAccount(requireContext(), callback = callback)
            }
        }
        binding.imgClose.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }
}