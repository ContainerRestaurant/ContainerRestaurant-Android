package container.restaurant.android.presentation.my

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import container.restaurant.android.R
import container.restaurant.android.databinding.FragmentMyFeedBinding
import container.restaurant.android.presentation.base.BaseFragment
import container.restaurant.android.presentation.feed.detail.FeedDetailActivity
import container.restaurant.android.util.DataTransfer
import container.restaurant.android.util.EventObserver
import container.restaurant.android.util.SharedPrefUtil
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyFeedFragment : BaseFragment<FragmentMyFeedBinding, MyViewModel>() {

    override val layoutResId: Int = R.layout.fragment_my_feed
    override val viewModel: MyViewModel by viewModel()

    private val args: MyFeedFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.viewModel = viewModel
        setBindItem()
        getMyFeedList()
        observeData()
    }

    private fun setBindItem() {
        viewDataBinding.apply {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun observeData() {
        with(viewModel) {
            isExploreFeedItemClicked.observe(viewLifecycleOwner, EventObserver{
                if(it){
                    val intent = FeedDetailActivity.getIntent(requireActivity())
                    intent.putExtra(DataTransfer.FEED_ID, selectedFeedId)
                    startActivity(intent)
                }
            })

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


    private fun getMyFeedList() {
        lifecycleScope.launchWhenCreated {
            val tokenBearer = SharedPrefUtil.getString(requireContext()) { TOKEN_BEARER }
            val userId = SharedPrefUtil.getInt(requireContext()) { USER_ID }
            viewModel.getMyFeedList(tokenBearer, userId)
        }
    }
}