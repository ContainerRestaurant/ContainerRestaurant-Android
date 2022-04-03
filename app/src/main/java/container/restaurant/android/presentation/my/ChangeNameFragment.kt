package container.restaurant.android.presentation.my

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import container.restaurant.android.R
import container.restaurant.android.data.request.UpdateProfileRequest
import container.restaurant.android.data.response.UpdateProfileResponse
import container.restaurant.android.databinding.FragmentChangeNameBinding
import container.restaurant.android.dialog.AlertDialog
import container.restaurant.android.presentation.auth.AuthViewModel
import container.restaurant.android.presentation.base.BaseFragment
import container.restaurant.android.presentation.user.UserProfileActivity
import container.restaurant.android.util.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ChangeNameFragment : BaseFragment<FragmentChangeNameBinding, AuthViewModel>() {
    override val layoutResId: Int = R.layout.fragment_change_name
    override val viewModel: AuthViewModel by sharedViewModel()

    private val nicknameEditing = MutableStateFlow("")

    private var nicknameFirstEdited = true

    private val args: ChangeNameFragmentArgs by navArgs()

    companion object {
        private const val DEBOUNCE_TIME = 250L
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.viewModel = viewModel
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
            activity?.finish()
        })

        viewModel.isCompleteButtonClicked.observe(viewLifecycleOwner, EventObserver {
            lifecycleScope.launchWhenCreated {
                val tokenBearer = SharedPrefUtil.getString(requireContext()) { TOKEN_BEARER }
                val userId = SharedPrefUtil.getInt(requireContext()) { USER_ID }
                viewModel.updateProfile(tokenBearer, userId ,UpdateProfileRequest(nicknameEditing.value))
                findNavController().popBackStack()
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
        viewDataBinding.editNickname.setText(args.nickName)
        viewModel.infoMessage.value=""
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
                    else if(nickname == args.nickName) {
                        viewModel.infoMessage.value = getString(R.string.nickname_not_changed)
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

    private fun completeProfile(profileResponse: UpdateProfileResponse) {
        simpleDialog("Success", "닉네임이 변경되었습니다.", AlertDialog.SUCCESS_TYPE)
    }
}