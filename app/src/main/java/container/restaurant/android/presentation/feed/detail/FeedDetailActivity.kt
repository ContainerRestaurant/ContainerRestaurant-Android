package container.restaurant.android.presentation.feed.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import container.restaurant.android.R
import container.restaurant.android.databinding.ActivityFeedDetailBinding
import container.restaurant.android.presentation.base.BaseActivity
import container.restaurant.android.util.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class FeedDetailActivity : BaseActivity<ActivityFeedDetailBinding, FeedDetailViewModel>() {

    override val layoutResId: Int = R.layout.activity_feed_detail
    override val viewModel: FeedDetailViewModel by viewModel()

    private var feedAdapter: FragmentStateAdapter? = null
    private val tabText: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewDataBinding.viewModel = viewModel

        initData()
        observeData()
    }

    private fun initData() {
        initFeedId()
        getFeedDetail()
        getFeedCommentReply()
    }

    private fun initFeedId() {
        val feedId = intent.getIntExtra(DataTransfer.FEED_ID, -1)
        viewModel.feedId.value = feedId
    }

    private fun getFeedDetail() {
        lifecycleScope.launchWhenCreated {
            val tokenBearer = SharedPrefUtil.getString(appCompatActivity) { TOKEN_BEARER }
            val feedId = viewModel.feedId.value ?: -1
            viewModel.getFeedDetail(tokenBearer, feedId)
        }
    }

    private fun getFeedCommentReply() {
        lifecycleScope.launchWhenCreated {
            val tokenBearer = SharedPrefUtil.getString(appCompatActivity) { TOKEN_BEARER }
            val feedId = viewModel.feedId.value ?: -1
            viewModel.getFeedCommentReply(
                tokenBearer,
                feedId,
                onGetSuccess = {},
                onGetFail = {}
            )
        }
    }

    private fun observeData() {
        with(viewModel) {
            observe(ownerContainerLevel) { ownerLevelTitle ->
                if (ownerProfileUrl.value == null) {
                    ownerProfileRes.value =
                        CommUtils.getEmptyProfileResId(applicationContext, ownerLevelTitle)
                }
            }
            observe(content) {
                feedAdapter = getFragmentStateAdapter(it)
                setupFeedPager()
            }
            isBackButtonClicked.observe(this@FeedDetailActivity, EventObserver{
                if(it){
                    onBackPressed()
                }
            })
            isLikeButtonClicked.observe(appCompatActivity) {
                if(isLikeActivated.value == true) {
                    isLikeActivated.value = false
                    likeCount.value = likeCount.value?.minus(1)
                    cancelLikeFeed(viewModel.feedId.value ?: 0, isLikeActivated, likeCount)
                }
                else if(isLikeActivated.value == false) {
                    isLikeActivated.value = true
                    likeCount.value = likeCount.value?.plus(1)
                    likeFeed(viewModel.feedId.value ?: 0, isLikeActivated, likeCount)
                }
            }
            isScrapButtonClicked.observe(appCompatActivity) {
                if(isScrapActivated.value == true) {
                    isScrapActivated.value = false
                    scrapCount.value = scrapCount.value?.minus(1)
                    cancelScrapFeed(viewModel.feedId.value ?: 0, isScrapActivated, scrapCount)
                }
                else if(isScrapActivated.value == false) {
                    isScrapActivated.value = true
                    scrapCount.value = scrapCount.value?.plus(1)
                    scrapFeed(viewModel.feedId.value ?: 0, isScrapActivated, scrapCount)
                }
            }
        }
    }

    // 용기낸 소감(내용)이 있는지 없는 지에 따라 다른 페이저 어댑터 생성
    private fun getFragmentStateAdapter(content: String): FragmentStateAdapter {
        if (content.isNotEmpty()) {
            tabText.add("용기낸 소감")
            tabText.add("용기낸 정보")
            return object : FragmentStateAdapter(this@FeedDetailActivity) {
                override fun getItemCount(): Int = 2

                override fun createFragment(position: Int): Fragment = when (position) {
                    0 -> {
                        FeedDetailContentFragment()
                    }
                    else -> {
                        FeedDetailInfoFragment()
                    }
                }

            }
        } else {
            tabText.add("용기낸 정보")
            return object : FragmentStateAdapter(this@FeedDetailActivity) {
                override fun getItemCount(): Int = 1

                override fun createFragment(position: Int): Fragment {
                    return FeedDetailInfoFragment()
                }
            }
        }
    }

    private fun setupFeedPager() {
        viewDataBinding.viewPager.adapter = feedAdapter

//        viewDataBinding.viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
//            override fun onPageSelected(position: Int) {
//                super.onPageSelected(position)
//                val view = viewDataBinding.viewPager
//                view.post {
//                    val wMeasureSpec = View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY)
//                    val hMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
//                    view.measure(wMeasureSpec, hMeasureSpec)
//
//                    if (view.layoutParams.height != view.measuredHeight) {
//                        // ParentViewGroup is, for example, LinearLayout
//                        // ... or whatever the parent of the ViewPager2 is
//                        view.layoutParams = (view.layoutParams as ConstraintLayout.LayoutParams)
//                            .also { lp -> lp.height = view.measuredHeight }
//                    }
//                }
//            }
//        })

        // tabLayout 과 viewPager 를 연결
        TabLayoutMediator(viewDataBinding.tabLayout, viewDataBinding.viewPager) { tab, pos ->
            tab.text = tabText[pos]
        }.attach()

    }

    private fun likeFeed(feedId: Int, isLike: MutableLiveData<Boolean>, likeCnt: MutableLiveData<Int>) {
        lifecycleScope.launchWhenCreated {
            val tokenBearer = SharedPrefUtil.getString(appCompatActivity) { TOKEN_BEARER }
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
            val tokenBearer = SharedPrefUtil.getString(appCompatActivity) { TOKEN_BEARER }
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

    private fun scrapFeed(feedId: Int, isScrapped: MutableLiveData<Boolean>, scrapCnt: MutableLiveData<Int>) {
        lifecycleScope.launchWhenCreated {
            val tokenBearer = SharedPrefUtil.getString(appCompatActivity) { TOKEN_BEARER }
            viewModel.scrapFeed(
                tokenBearer,
                feedId,
                onLikeSuccess = {

                }, onLikeFail = {
                    toastShortOfFailMessage("피드 좋아요")
                    isScrapped.value = false
                    scrapCnt.value = scrapCnt.value?.minus(1)
                }
            )
        }
    }

    private fun cancelScrapFeed(feedId: Int, isScrapped: MutableLiveData<Boolean>, scrapCnt: MutableLiveData<Int>) {
        lifecycleScope.launchWhenCreated {
            val tokenBearer = SharedPrefUtil.getString(appCompatActivity) { TOKEN_BEARER }
            viewModel.cancelScrapFeed(
                tokenBearer,
                feedId,
                onCancelSuccess = {

                },
                onCancelFail = {
                    toastShortOfFailMessage("피드 좋아요 취소")
                    isScrapped.value = true
                    scrapCnt.value = scrapCnt.value?.plus(1)
                }
            )
        }
    }

    companion object {
        fun getIntent(activity: Activity) = Intent(activity, FeedDetailActivity::class.java)
    }
}