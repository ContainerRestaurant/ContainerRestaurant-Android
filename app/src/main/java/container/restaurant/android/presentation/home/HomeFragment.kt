package container.restaurant.android.presentation.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayoutMediator
import container.restaurant.android.R
import container.restaurant.android.data.response.UserInfoResponse
import container.restaurant.android.databinding.FragmentHomeBinding
import container.restaurant.android.dialog.SimpleConfirmDialog
import container.restaurant.android.presentation.auth.KakaoSignInDialogFragment
import container.restaurant.android.presentation.base.BaseFragment
import container.restaurant.android.presentation.feed.all.FeedAllActivity
import container.restaurant.android.presentation.feed.detail.FeedDetailActivity
import container.restaurant.android.presentation.feed.write.FeedWriteActivity
import container.restaurant.android.presentation.home.item.BannerAdapter
import container.restaurant.android.presentation.user.UserProfileActivity
import container.restaurant.android.util.*
import org.koin.androidx.viewmodel.ext.android.viewModel

internal class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {
    override val layoutResId: Int = R.layout.fragment_home
    private val bannerAdapter by lazy {
        BannerAdapter()
    }

    override val viewModel: HomeViewModel by viewModel()

    // 가입 완료후 업데이트 할 정보
    private fun updateData() {
        lifecycleScope.launchWhenCreated {
            val tokenBearer = SharedPrefUtil.getString(requireContext()) { TOKEN_BEARER }
            viewModel.getHomeInfo(tokenBearer)
        }
    }
    private val signUpResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        updateData()
    }

    // 피드 쓰기 결과 받고 업데이트 해야함
    private val feedWriteResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == Activity.RESULT_OK) {
                lifecycleScope.launchWhenCreated {
                    val tokenBearer = SharedPrefUtil.getString(requireContext()) { TOKEN_BEARER }
                    viewModel.getHomeInfo(tokenBearer)
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.viewModel = viewModel
        observeData()
        setUpBannerView()
        getHomeInfo()
        getRecommendedFeedList()
    }

    private fun observeData() {
        with(viewModel) {
            isNavToAllContainerFeedClicked.observe(viewLifecycleOwner) {
                startActivity(FeedAllActivity.getIntent(requireContext()))
            }
            isNavToMyContainerFeedClicked.observe(viewLifecycleOwner) {
                logInCheckAndSeeMyCourage()
            }
            userLevelTitle.observe(viewLifecycleOwner) { userLevelTitle ->
                if (userProfileUrl.value == null) {
                    setUserProfileResByLevelTitle(requireContext(), userProfileRes, userLevelTitle)
                }
                setHomeIconByLevelTitle(requireContext(), homeIconResByUserLevel, userLevelTitle)
            }
            isExploreFeedItemClicked.observe(viewLifecycleOwner) {
                val intent = FeedDetailActivity.getIntent(requireActivity())
                    .apply {
                        putExtra(DataTransfer.FEED_ID, it)
                    }
                startActivity(intent)
            }

            isLikeFeedItemClicked.observe(viewLifecycleOwner) {
                if(it.isLike.value == true) {
                    it.isLike.value = false
                    it.likeCnt.value = it.likeCnt.value?.minus(1)
                    cancelLikeFeed(it.feedPreviewDto.id, it.isLike, it.likeCnt)
                }
                else if(it.isLike.value == false) {
                    it.isLike.value = true
                    it.likeCnt.value = it.likeCnt.value?.plus(1)
                    likeFeed(it.feedPreviewDto.id, it.isLike, it.likeCnt)
                }
            }
        }

        observe(viewModel.bannerList) {
            addBannerItems()
        }

        observe(viewModel.recommendedFeedList) {

        }
    }

    private fun addBannerItems() {
        viewModel.bannerList.value?.let {
            bannerAdapter.addItems(it)
        }

    }

    private fun setUpBannerView() {
        viewDataBinding.pagerIntroBanner.adapter = bannerAdapter
        TabLayoutMediator(viewDataBinding.tablayoutIndicator, viewDataBinding.pagerIntroBanner) { _, _ ->
        }.attach()
    }

    private fun getHomeInfo() {
        lifecycleScope.launchWhenCreated {
            val tokenBearer = SharedPrefUtil.getString(requireContext()) { TOKEN_BEARER }
            viewModel.getHomeInfo(tokenBearer)
        }
    }

    private fun getRecommendedFeedList() {
        lifecycleScope.launchWhenCreated {
            val tokenBearer = SharedPrefUtil.getString(requireContext()) { TOKEN_BEARER }
            viewModel.getRecommendedFeedList(tokenBearer)
        }
    }

    private fun showMyContainerConfirmDialog() {
        val dialog = SimpleConfirmDialog(
            titleStr = "용기낸 경험을 들려주시겠어요?",
            rightBtnStr = "네, 좋아요!",
            leftBtnStr = "나중에요"
        )
        dialog.setMultiEventListener(object : SimpleConfirmDialog.MultiEventListener {
            override fun onRightBtnClick(dialogSelf: SimpleConfirmDialog) {
                // 로그인 성공 했을 때 동작
                val onSignInSuccess: (UserInfoResponse) -> Unit = {
                    lifecycleScope.launchWhenCreated {
                        val tokenBearer = SharedPrefUtil.getString(requireContext()) { TOKEN_BEARER }
                        viewModel.getHomeInfo(tokenBearer)
                    }
                }
                dialogSelf.dismiss()
                if (!SharedPrefUtil.getBoolean(requireContext()) { IS_USER_LOGIN }) {
                    KakaoSignInDialogFragment(
                        onSignInSuccess = onSignInSuccess,
                        onClose = { parentFragment?.parentFragmentManager?.popBackStack() }
                    ).apply {
                        show(
                            childFragmentManager,
                            tag
                        )
                    }
                } else {
                    feedWriteResultLauncher.launch(Intent(requireContext(), FeedWriteActivity::class.java))
                }
            }

            override fun onLeftBtnClick(dialogSelf: SimpleConfirmDialog) {
                dialogSelf.dismiss()
            }
        })
        dialog.show(childFragmentManager, "SimpleConfirmDialog")
    }

    private fun logInCheckAndSeeMyCourage() {
        // 로그인 성공 했을 때 동작
        val onSignInSuccess: (UserInfoResponse) -> Unit = {
            if (it.feedCount == 0) {
                showMyContainerConfirmDialog()
            }
            // 사용자 피드보여주기
            else {
                startActivity(UserProfileActivity.getIntent(requireContext()))
            }
            lifecycleScope.launchWhenCreated {
                val tokenBearer = SharedPrefUtil.getString(requireContext()) { TOKEN_BEARER }
                viewModel.getHomeInfo(tokenBearer)
            }
        }


        // 프로젝트에 저장된 토큰 없을 때
        if (!SharedPrefUtil.getBoolean(requireContext()) { IS_USER_LOGIN }) {
            val kakaoSignInDialogFragment = KakaoSignInDialogFragment(
                onSignInSuccess = onSignInSuccess,
                onClose = { parentFragment?.parentFragmentManager?.popBackStack() }
            )
            kakaoSignInDialogFragment.show(childFragmentManager, kakaoSignInDialogFragment.tag)
        }

        // 프로젝트에 저장된 토큰 있을 때
        else {
            lifecycleScope.launchWhenCreated {
                val tokenBearer = SharedPrefUtil.getString(requireContext()) { TOKEN_BEARER }
                viewModel.signInWithAccessToken(
                    tokenBearer,
                    onNicknameNull = {},
                    onSignInSuccess = onSignInSuccess,
                    onInvalidToken = {}
                )
            }
        }
    }

    private fun likeFeed(feedId: Int, isLike: MutableLiveData<Boolean>, likeCnt: MutableLiveData<Int>) {
        lifecycleScope.launchWhenCreated {
            val tokenBearer = SharedPrefUtil.getString(requireContext()) { TOKEN_BEARER }
            viewModel.likeFeed(
                tokenBearer,
                feedId,
                onLikeSuccess = {

                }, onLikeFail = {
                    toastShortOfFailMessage("피드 좋아요")
                    isLike.value = false
                    likeCnt.value = likeCnt.value?.minus(1)
                }
            )
        }
    }

    private fun cancelLikeFeed(feedId: Int, isLike: MutableLiveData<Boolean>, likeCnt: MutableLiveData<Int>) {
        lifecycleScope.launchWhenCreated {
            val tokenBearer = SharedPrefUtil.getString(requireContext()) { TOKEN_BEARER }
            viewModel.cancelLikeFeed(
                tokenBearer,
                feedId,
                onCancelSuccess = {

                },
                onCancelFail = {
                    toastShortOfFailMessage("피드 좋아요 취소")
                    isLike.value = true
                    likeCnt.value = likeCnt.value?.plus(1)
                }
            )
        }
    }

    companion object {
        fun newInstance(): HomeFragment = HomeFragment()
    }
}