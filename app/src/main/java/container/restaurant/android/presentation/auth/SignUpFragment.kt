package container.restaurant.android.presentation.auth

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import container.restaurant.android.R
import container.restaurant.android.databinding.FragmentSignUpBinding
import container.restaurant.android.presentation.base.BaseFragment
import container.restaurant.android.presentation.user.UserProfileActivity
import container.restaurant.android.util.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import org.koin.androidx.viewmodel.ext.android.viewModel

internal class SignUpFragment : BaseFragment<FragmentSignUpBinding, AuthViewModel>() {
    override val layoutResId: Int = R.layout.fragment_sign_up
    override val viewModel: AuthViewModel by viewModel()

    private val nicknameEditing = MutableStateFlow("")

    var nicknameFirstEdited = true

    private val provider: String? by lazy {
        arguments?.getString(DataTransfer.PROVIDER)
    }

    private val accessToken: String? by lazy {
        arguments?.getString(DataTransfer.ACCESS_TOKEN)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.viewModel = viewModel
        viewDataBinding.lifecycleOwner = viewLifecycleOwner

        observeData()
        setupNicknameEditing()
    }

    private fun observeData(){
        // 백엔드 중복성 검사 요청후 결과에 따른 처리
        observe(viewModel.nicknameDuplicationCheckResult) { response ->
            if(response.exists == false){
                viewModel.infoMessage.value = getString(R.string.nickname_possible)
                setBtnCompleteValidation(true)
            }
            else {
                viewModel.infoMessage.value = getString(R.string.nickname_duplication)
                setBtnCompleteValidation(true)
            }
        }


        viewModel.errorMessageId.observe(viewLifecycleOwner, EventObserver {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            startActivity(UserProfileActivity.getIntent(requireContext()))
            activity?.finish()
        })

        viewModel.isCompleteButtonClicked.observe(viewLifecycleOwner, EventObserver {
            lifecycleScope.launchWhenCreated {
                if(provider!=null && accessToken!=null){
                    viewModel.generateAccessToken(provider!!, accessToken!!
                        ,onGenerateSuccess = { token, userId ->
                            SharedPrefUtil.setBoolean(requireContext(), { IS_USER_LOGIN }, true)
                            if(token != null) SharedPrefUtil.setString(requireContext(), { TOKEN_BEARER }, "Bearer $token")
                            if(userId != null) SharedPrefUtil.setInt(requireContext(), { USER_ID }, userId)
                            requireActivity().apply {
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                        }, onGenerateFail = {
                            toastShortOfFailMessage("회원가입 혹은 로그인")
                        })
                }
            }
        })
    }

    //버튼 활성화 설정
    private fun setBtnCompleteValidation(isValidate: Boolean) {
        if(isValidate) {
            viewDataBinding.tvNicknameError.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_03))
        }
        else {
            viewDataBinding.tvNicknameError.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange_02))
        }
        viewDataBinding.btnComplete.isActivated = isValidate
    }

    //nickname 입력 리스너 설정
    private fun setupNicknameEditing() {
        viewDataBinding.editNickname.doOnTextChanged { text, _, _, _ ->
            nicknameEditing.value = text.toString()
            // 사용자가 처음 수정 이후에 안내 메세지를 보여주도록 함
            if(nicknameFirstEdited) {
                nicknameValidationCheck()
                nicknameFirstEdited = false
            }
        }
    }

    // nicknameEditing 값이 변경되면 유효성 검사를 실행 후, 백엔드에 요청하여 중복 확인
    private fun nicknameValidationCheck() {
        lifecycleScope.launchWhenCreated {
            nicknameEditing
                .debounce(DEBOUNCE_TIME)
                .filter { nickname ->
                    if (nickname.isEmpty()) {
                        viewModel.infoMessage.value = getString(R.string.nickname_empty)
                        setBtnCompleteValidation(false)
                        return@filter false
                    }
                    else if(!letterValidationCheck(nickname)){
                        viewModel.infoMessage.value = getString(R.string.nickname_letter_impossible)
                        setBtnCompleteValidation(false)
                        return@filter false
                    }
                    else if(!lengthShortValidationCheck(nickname)){
                        viewModel.infoMessage.value = getString(R.string.nickname_too_short)
                        setBtnCompleteValidation(false)
                        return@filter false
                    }
                    else if(!lengthLongValidationCheck(nickname)){
                        viewModel.infoMessage.value = getString(R.string.nickname_too_long)
                        setBtnCompleteValidation(false)
                        return@filter false
                    }
                    true
                }
                .collect { nickname ->
                    viewModel.nicknameDuplicationCheck(nickname)
                }
        }
    }

    companion object {
        private const val DEBOUNCE_TIME = 250L
        fun newInstance() = SignUpFragment()
    }
}