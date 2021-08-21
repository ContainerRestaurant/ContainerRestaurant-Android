package container.restaurant.android.presentation.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import container.restaurant.android.R
import container.restaurant.android.databinding.FragmentFeedBinding
import container.restaurant.android.databinding.TabFeedCategoryBinding
import container.restaurant.android.presentation.feed.category.FeedCategory
import container.restaurant.android.presentation.feed.category.FeedCategoryFragment
import container.restaurant.android.presentation.feed.category.FeedCategoryViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

internal class FeedFragment : Fragment() {

    private val viewModel: FeedExploreViewModel by viewModel()

    private val tabViewModelMap = HashMap<Int, FeedCategoryViewModel>()

    private var currentTabPos = 0

    private lateinit var feedAdapter: FragmentStateAdapter

    private lateinit var binding: FragmentFeedBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFeedBinding.inflate(layoutInflater, container, false)
            .apply {
                this.viewModel = this@FeedFragment.viewModel
                this.lifecycleOwner = this@FeedFragment
            }
        feedAdapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return FeedCategory.values().size
            }

            override fun createFragment(position: Int): Fragment {
                currentTabPos = position
                return FeedCategoryFragment.newInstance(FeedCategory.values()[position]).apply {
                    setOnViewModelCreatedListener(object : FeedCategoryFragment.OnViewModelCreatedListener {
                        override fun onViewModelCreated(feedCategoryViewModel: FeedCategoryViewModel) {
                            tabViewModelMap[position] = feedCategoryViewModel
                        }
                    })
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFeedPager()
        setupTabLayoutListener()
    }

    private fun setupFeedPager() {
        binding.pagerFeed.adapter = feedAdapter

        TabLayoutMediator(binding.tablayoutFeedType, binding.pagerFeed) { tab, pos ->
            tab.customView = createTab(pos)
        }.attach()
    }

    private fun createTab(pos: Int): View {
        val binding = TabFeedCategoryBinding.inflate(layoutInflater, null, false)
        binding.tabTitle.text = FeedCategory.values()[pos].type
        if (pos == 0) {
            binding.tabTitle.typeface = ResourcesCompat.getFont(requireContext(), R.font.spoqa_hans_sans_bold)
        }

        return binding.root
    }

    private fun setupTabLayoutListener() {
        binding.tablayoutFeedType.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                setTabTitleFont(tab, R.font.spoqa_hans_sans_regular)
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                setTabTitleFont(tab, R.font.spoqa_hans_sans_bold)
            }
        })
    }

    private fun setTabTitleFont(tab: TabLayout.Tab?, font: Int) {
        val tabTitleTextView = tab?.customView?.findViewById<TextView>(R.id.tab_title)
        tabTitleTextView?.typeface = ResourcesCompat.getFont(requireContext(), font)
    }

    companion object {
        fun newInstance(): FeedFragment = FeedFragment()
    }
}