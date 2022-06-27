package container.restaurant.android.presentation.feed.explore

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenCreated
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import container.restaurant.android.R
import container.restaurant.android.data.FeedCategory
import container.restaurant.android.data.SortingCategory
import container.restaurant.android.databinding.FragmentFeedExploreBinding
import container.restaurant.android.databinding.ItemSortBinding
import container.restaurant.android.databinding.TabFeedCategoryBinding
import container.restaurant.android.presentation.base.BaseFragment
import container.restaurant.android.presentation.feed.category.FeedCategoryFragment
import container.restaurant.android.util.ContainerFragmentStateAdapter
import container.restaurant.android.util.observeFragmentData
import org.koin.androidx.viewmodel.ext.android.viewModel

internal class FeedExploreFragment : BaseFragment<FragmentFeedExploreBinding, FeedExploreViewModel>() {

    override val layoutResId: Int = R.layout.fragment_feed_explore
    override val viewModel: FeedExploreViewModel by viewModel()
    val listFragment = mutableListOf<FeedCategoryFragment>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.viewModel = viewModel
        viewDataBinding.lifecycleOwner = viewLifecycleOwner

        setUpTabLayouts()
        observeData()
    }

    private fun setUpTabLayouts() {
        setUpFeedTypeTabLayoutAndPager()
        setUpFeedSortTypeTabLayout()
    }

    private fun setUpFeedTypeTabLayoutAndPager() {
        listFragment.clear()
        FeedCategory.values().forEach {
            val feedCategoryFragment = FeedCategoryFragment.newInstance(it)
            observeFragmentData(feedCategoryFragment, requireActivity()) {
                selectedSortingCategory.observe(viewLifecycleOwner) {
                    viewModel.selectedSortingCategory.value = it
                }
            }
            listFragment.add(feedCategoryFragment)
        }

        val pagerAdapter = ContainerFragmentStateAdapter(childFragmentManager, lifecycle)
        pagerAdapter.addFragments(listFragment)
        viewDataBinding.pagerFeed.adapter = pagerAdapter

        // tabLayout 과 viewPager 를 연결
        TabLayoutMediator(viewDataBinding.tabLayoutFeedType, viewDataBinding.pagerFeed) { tab, pos ->
            val binding = TabFeedCategoryBinding.inflate(layoutInflater, null, false)
                .apply {
                    item = viewModel.feedCategories[pos]
                    lifecycleOwner = this@FeedExploreFragment
                }
            tab.customView = binding.root
        }.attach()

        viewDataBinding.tabLayoutFeedType.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                for(index in 0 until viewDataBinding.tabLayoutFeedType.tabCount) {
                    if (tab==viewDataBinding.tabLayoutFeedType.getTabAt(index)) {
                        lifecycleScope.launchWhenCreated {
                            listFragment[index].whenCreated {
                                viewModel.selectedSortingCategory.value = listFragment[index].viewModel.selectedSortingCategory.value
                            }
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
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
        viewDataBinding.tabLayoutFeedSortType.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val selectedSortingCategory = SortingCategory.getInstanceByTitle(tab?.text.toString())
                listFragment[viewDataBinding.tabLayoutFeedType.selectedTabPosition]
                    .viewModel.selectedSortingCategory.value = selectedSortingCategory
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun observeData() {
        with(viewModel) {
            selectedSortingCategory.observe(viewLifecycleOwner) {
                for(index in 0 until viewDataBinding.tabLayoutFeedSortType.tabCount) {
                    val iteratingTab = viewDataBinding.tabLayoutFeedSortType.getTabAt(index)
                    if(iteratingTab?.text == it?.title) {
                        viewDataBinding.tabLayoutFeedSortType.selectTab(iteratingTab)
                        break
                    }
                }
            }
        }
    }
    
    companion object {
        fun newInstance(): FeedExploreFragment = FeedExploreFragment()
    }
}