package container.restaurant.android.presentation.feed.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import container.restaurant.android.R
import container.restaurant.android.data.FeedCategory
import container.restaurant.android.data.SortingCategory
import container.restaurant.android.databinding.FragmentFeedCategoryBinding
import container.restaurant.android.presentation.base.BaseFragment
import container.restaurant.android.presentation.feed.detail.FeedDetailActivity
import container.restaurant.android.presentation.feed.item.ContainerFeedAdapter
import container.restaurant.android.util.DataTransfer
import container.restaurant.android.util.EventObserver
import container.restaurant.android.util.SharedPrefUtil
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


internal class FeedCategoryFragment : BaseFragment<FragmentFeedCategoryBinding, FeedCategoryViewModel>() {
    override val layoutResId: Int
        get() = R.layout.fragment_feed_category

    override val viewModel: FeedCategoryViewModel by viewModel {
        parametersOf(feedCategory)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.viewModel = viewModel
        lifecycleScope.launchWhenCreated {
            val tokenBearer = SharedPrefUtil.getString(requireContext()) { TOKEN_BEARER }
            viewModel.getFeedList(tokenBearer, SortingCategory.LATEST)
        }
        observeData()
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

    private var feedCategory: FeedCategory? = null

    companion object {
        fun newInstance(feedCategory: FeedCategory) = FeedCategoryFragment()
            .apply {
                this.feedCategory = feedCategory
            }
    }
}