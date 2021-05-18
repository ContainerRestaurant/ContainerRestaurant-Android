package container.restaurant.android.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.tabs.TabLayoutMediator
import com.tak8997.github.domain.BannerContent
import container.restaurant.android.databinding.FragmentHomeBinding
import container.restaurant.android.presentation.auth.SignInActivity
import container.restaurant.android.presentation.feed.all.FeedAllActivity
import container.restaurant.android.presentation.feed.item.ContainerFeedAdapter
import container.restaurant.android.presentation.home.item.BannerAdapter
import container.restaurant.android.util.observe
import org.koin.androidx.viewmodel.ext.android.viewModel

internal class HomeFragment : Fragment() {

    private val bannerAdapter by lazy {
        BannerAdapter()
    }

    private val containerFeedAdapter by lazy {
        ContainerFeedAdapter()
    }

    private val viewModel: HomeViewModel by viewModel()

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
            .apply {
                lifecycleOwner = this@HomeFragment
                viewModel = this@HomeFragment.viewModel
            }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(viewModel) {
            navToMyContainerFeed.observe(viewLifecycleOwner) {

            }
            navToAllContainerFeed.observe(viewLifecycleOwner) {
                startActivity(FeedAllActivity.getIntent(requireContext()))
            }
            kakaoLoginDialog.observe(viewLifecycleOwner) {
//                KakaoSignInDialogFragment().show(childFragmentManager, "KakaoSignInDialogFragment")
                startActivity(SignInActivity.getIntent(requireContext()))
            }
        }

        setupBannerPager()
        setupContainerFeedRecycler()
    }

    private fun setupContainerFeedRecycler() {
        with(binding.rvContainerFeedHistory) {
            layoutManager = GridLayoutManager(context ?: return, 2)
            adapter = containerFeedAdapter
        }
    }

    private fun setupBannerPager() {
        binding.pagerIntroBanner.adapter = bannerAdapter
        TabLayoutMediator(binding.tablayoutIndicator, binding.pagerIntroBanner) { _, _ ->
        }.attach()
        // test dummy
        bannerAdapter.addItems(
            listOf(
                BannerContent("this is title", "this is content"),
                BannerContent("this is title", "this is content"),
                BannerContent("this is title", "this is content")
            )
        )
    }

    companion object {
        fun newInstance(): HomeFragment = HomeFragment()
    }
}