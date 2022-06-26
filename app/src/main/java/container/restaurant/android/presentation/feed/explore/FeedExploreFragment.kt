package container.restaurant.android.presentation.feed.explore

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.tbuonomo.viewpagerdotsindicator.setPaddingVertical
import container.restaurant.android.R
import container.restaurant.android.data.FeedCategory
import container.restaurant.android.data.SortingCategory
import container.restaurant.android.databinding.FragmentFeedExploreBinding
import container.restaurant.android.databinding.ItemSortBinding
import container.restaurant.android.databinding.TabFeedCategoryBinding
import container.restaurant.android.presentation.base.BaseFragment
import container.restaurant.android.presentation.feed.category.FeedCategoryFragment
import container.restaurant.android.util.observeFragmentData
import org.koin.androidx.viewmodel.ext.android.viewModel

internal class FeedExploreFragment : BaseFragment<FragmentFeedExploreBinding, FeedExploreViewModel>() {

    override val layoutResId: Int = R.layout.fragment_feed_explore
    override val viewModel: FeedExploreViewModel by viewModel()

    private val feedAdapter: FragmentStateAdapter by lazy {
        object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int
                = FeedCategory.values().size

            override fun createFragment(position: Int): Fragment
            {
                val fragment = FeedCategoryFragment.newInstance(FeedCategory.values()[position])
                observeFragmentData(fragment, requireActivity()) {
                    selectedFeedCategory.observe(viewLifecycleOwner) {
                        viewModel.selectedSortingCategory.value = it
                    }
                }
                return fragment
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.viewModel = viewModel
        viewDataBinding.lifecycleOwner = viewLifecycleOwner

        setUpTabLayouts()
    }

    private fun setUpTabLayouts() {
        setUpFeedTypeTabLayoutAndPager()
        setUpFeedSortTypeTabLayout()
    }

    private fun setUpFeedTypeTabLayoutAndPager() {
        viewDataBinding.pagerFeed.adapter = feedAdapter

        // tabLayout 과 viewPager 를 연결
        TabLayoutMediator(viewDataBinding.tabLayoutFeedType, viewDataBinding.pagerFeed) { tab, pos ->
            val binding = TabFeedCategoryBinding.inflate(layoutInflater, null, false)
                .apply {
                    item = viewModel.feedCategories[pos]
                    lifecycleOwner = this@FeedExploreFragment
                }
            tab.customView = binding.root
        }.attach()
    }

    private fun setUpFeedSortTypeTabLayout() {
        SortingCategory.values().forEach {
            val tab = viewDataBinding.tabLayoutFeedSortType.newTab()
            tab.customView = ItemSortBinding.inflate(layoutInflater, viewDataBinding.root as ViewGroup, false)
                .apply {
                    tvSortTitle.text = it.title
                }
                .root
            tab.text = it.title
            viewDataBinding.tabLayoutFeedSortType.addTab(tab)
        }
        viewDataBinding.tabLayoutFeedSortType.minimumHeight = 0
    }

    private fun observeData() {
        with(viewModel) {
            selectedSortingCategory.observe(viewLifecycleOwner) {

            }
        }
    }
    
    companion object {
        fun newInstance(): FeedExploreFragment = FeedExploreFragment()
    }
}