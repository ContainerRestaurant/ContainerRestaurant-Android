package container.restaurant.android.presentation.my

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import container.restaurant.android.R
import container.restaurant.android.data.SharedPrefStorage
import container.restaurant.android.data.response.UserInfoResponse
import container.restaurant.android.databinding.FragmentMyHomeBinding
import container.restaurant.android.presentation.auth.KakaoSignInDialogFragment
import container.restaurant.android.presentation.base.BaseFragment
import container.restaurant.android.util.EventObserver
import container.restaurant.android.util.observe
import container.restaurant.android.util.setUserProfileResByLevelTitle
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyHomeFragment : BaseFragment<FragmentMyHomeBinding, MyViewModel>() {

    override val layoutResId: Int = R.layout.fragment_my_home
    override val viewModel: MyViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.viewModel = viewModel
        observeData()
        logInCheck()
    }

    // 가입 완료후 업데이트 할 정보
    private fun updateData(userInfoResponse: UserInfoResponse) {
        with(viewModel) {
            userNickName.value = userInfoResponse.nickname
            userFeedCount.value = userInfoResponse.feedCount
            userProfileUrl.value = userInfoResponse.profile
            userLevelTitle.value = userInfoResponse.levelTitle
            userScrapCount.value = userInfoResponse.scrapCount
            userId.value = userInfoResponse.id
            userEmail.value = userInfoResponse.email
            userBookmarkedCount.value = userInfoResponse.bookmarkedCount
        }
    }

    private fun logInCheck() {
        // 로그인 성공 했을 때 동작
        val onSignInSuccess: (UserInfoResponse) -> Unit = {
            updateData(it)
        }

        // 프로젝트에 저장된 토큰 없을 때
        if (!SharedPrefStorage(requireContext()).isUserSignIn) {
            val kakaoSignInDialogFragment = KakaoSignInDialogFragment(
                onSignInSuccess = onSignInSuccess,
                onClose = { parentFragment?.parentFragmentManager?.popBackStack() }
            )
            kakaoSignInDialogFragment.show(childFragmentManager, kakaoSignInDialogFragment.tag)
        }

        // 프로젝트에 저장된 토큰 있을 때
        else {
            lifecycleScope.launchWhenCreated {
                viewModel.signInWithAccessToken(
                    onNicknameNull = {},
                    onSignInSuccess = onSignInSuccess,
                    onInvalidToken = {}
                )
            }
        }
    }

    private fun observeData() {
        with(viewModel) {
            isSettingButtonClicked.observe(viewLifecycleOwner, EventObserver {
                if (it) {
                    navigateToSetting()
                }
            })
            isMyFeedButtonClicked.observe(viewLifecycleOwner, EventObserver {
                if (it) {
                    navigateToMyFeed()
                }
            })
            isScrapFeedButtonClicked.observe(viewLifecycleOwner, EventObserver {
                if (it) {
                    navigateToScrap()
                }
            })
            isBookmarkedRestaurantButtonClicked.observe(viewLifecycleOwner, EventObserver {
                if (it) {
                    navigateToBookmarkedRestaurant()
                }
            })
            isNicknameChangeButtonClicked.observe(viewLifecycleOwner, EventObserver {
                if (it) {
                    navigateToChangeName()
                }
            })
            observe(userLevelTitle) { userLevelTitle ->
                if (userProfileUrl.value == null) {
                    setUserProfileResByLevelTitle(requireContext(), userProfileRes, userLevelTitle)
                }
            }
        }
    }

    private fun navigateToChangeName() {
        with(viewModel) {
            if (userId.value != null && userNickName.value != null) {
                val directions = MyHomeFragmentDirections.actionMyHomeFragmentToChangeNameFragment(
                    userId.value!!,
                    viewModel.userNickName.value!!
                )
                findNavController().navigate(directions)
            } else {
                Toast.makeText(requireContext(), "유효하지 않은 아이디와 닉네임입니다.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun navigateToMyFeed() {
        viewModel.userId.value?.let {
            val directions = MyHomeFragmentDirections.actionMyHomeFragmentToMyFeedFragment(it)
            findNavController().navigate(directions)
        }
    }

    private fun navigateToScrap() {
        viewModel.userId.value?.let {
            val directions = MyHomeFragmentDirections.actionMyHomeFragmentToMyScrapFragment(it)
            findNavController().navigate(directions)
        }
    }

    private fun navigateToBookmarkedRestaurant() {
        val directions = MyHomeFragmentDirections.actionMyHomeFragmentToFavoriteFragment()
        findNavController().navigate(directions)
    }

    private fun navigateToSetting() {
        val directions = MyHomeFragmentDirections.actionMyHomeFragmentToMySettingFragment()
        findNavController().navigate(directions)
    }
}