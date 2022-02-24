package container.restaurant.android.presentation.auth

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import container.restaurant.android.R
import container.restaurant.android.data.response.UserInfoResponse
import container.restaurant.android.databinding.FragmentKakaoSigninBinding
import container.restaurant.android.util.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber


class KakaoSignInDialogFragment(val onSignInSuccess:(UserInfoResponse)->Unit,val onClose:() -> Unit) : DialogFragment() {

    private lateinit var binding: FragmentKakaoSigninBinding
    val viewModel: KakaoSignInDialogViewModel by viewModel()

    private lateinit var provider: String
    private lateinit var kakaoAccessToken: String

    private val signUpResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        // 가입 성공했을 때 처리
        if(it.resultCode == Activity.RESULT_OK) {
            viewModel.userInfoResponse.value?.let { userInfoResponse ->
                onSignInSuccess(userInfoResponse)
            }
        }
    }

    private val kakaoUserApi by lazy {
        UserApiClient.instance
    }

    private val kakaoCallback: (OAuthToken?, Throwable?) -> Unit = { token, err ->
        if (err != null) {
            Timber.e(err, "카카오 인증 실패")
            dismiss()
        } else if (token != null) {
            kakaoUserApi.me { userKakao, err2 ->
                if (err2 != null) {
                    Timber.e(err2, "카카오 사용자 정보 요청 실패")
                    dismiss()
                } else if (userKakao != null) {
                    Timber.d("카카오 인증 성공")
                    provider = Provider.KAKAO.providerStr
                    kakaoAccessToken = token.accessToken
                    lifecycleScope.launchWhenCreated {
                        viewModel.generateAccessToken(
                            Provider.KAKAO.providerStr,
                            token.accessToken,
                            {
                                signInWithAccessToken()
                            },
                            onGenerateFail = {
                                Toast.makeText(requireContext(), "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }

    private fun signInWithAccessToken() {
        requireActivity().lifecycleScope.launchWhenCreated {
            val tokenBearer = viewModel.tokenBearer.value ?: ""
            viewModel.signInWithAccessToken(
                tokenBearer,
                onNicknameNull = {
                    signUpResultLauncher.launch(
                        Intent(
                            requireActivity(),
                            SignUpActivity::class.java
                        )
                    )
                    dismiss()
                },
                onSignInSuccess = {
                    onSignInSuccess(it)
                    dismiss()
                }
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentKakaoSigninBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        observeData()
        setOnClickListeners()
    }

    private fun observeData() {
        with(viewModel) {
            errorMessageId.observe(this@KakaoSignInDialogFragment, EventObserver {
                Toast.makeText(requireContext(), getString(it), Toast.LENGTH_LONG).show()
                this@KakaoSignInDialogFragment.dismiss()
            })
        }
    }

    //다이얼로그 바깥 쪽 터치 시 호출됨
    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        onClose()
    }

    private fun kakaoLogin() {
        if (kakaoUserApi.isKakaoTalkLoginAvailable(requireContext())) {
            kakaoUserApi.loginWithKakaoTalk(requireContext(), callback = kakaoCallback)
        } else {
            kakaoUserApi.loginWithKakaoAccount(requireContext(), callback = kakaoCallback)
        }
    }

    private fun setOnClickListeners() {
        binding.tvKakaoLogin.setOnClickListener {
            kakaoLogin()
        }

        //dismiss 는 cancel 과 다른 것 확인함!
        binding.imgClose.setOnClickListener {
            onClose()
            dismissAllowingStateLoss()
        }
    }
}