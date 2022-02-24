package container.restaurant.android.presentation

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.annotation.MenuRes
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import container.restaurant.android.R
import container.restaurant.android.data.response.UserInfoResponse
import container.restaurant.android.databinding.ActivityMainBinding
import container.restaurant.android.presentation.auth.AuthViewModel
import container.restaurant.android.presentation.auth.KakaoSignInDialogFragment
import container.restaurant.android.presentation.base.BaseActivity
import container.restaurant.android.presentation.feed.explore.FeedExploreFragment
import container.restaurant.android.presentation.feed.write.FeedWriteActivity
import container.restaurant.android.presentation.home.HomeFragment
import container.restaurant.android.presentation.map.MapsFragment
import container.restaurant.android.util.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber

internal class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    private val navigationController: NavigationController by inject { parametersOf(this) }

    override val layoutResId: Int = R.layout.activity_main
    override val viewModel: MainViewModel by viewModel()
    private val authViewModel: AuthViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewDataBinding.viewModel = viewModel

        with(viewModel) {
            isFeedWriteClicked.observe(this@MainActivity) {
                logInAndFeedWrite()
            }
        }

        //백 스택을 이용하여 마이 페이지 로그인 취소 시 이전 탭으로 돌아가도록 설정
        supportFragmentManager.addOnBackStackChangedListener {
            //마이 페이지 상태가 아닐 때만 탭 변경하도록 함
            if(supportFragmentManager.findFragmentByTag(NavHostFragment::class.java.simpleName)==null){
                when {
                    supportFragmentManager.findFragmentByTag(HomeFragment::class.java.simpleName)!=null -> {
                        viewDataBinding.bottomNav.selectedItemId = BottomNavItem.HOME.menuId
                    }
                    supportFragmentManager.findFragmentByTag(FeedExploreFragment::class.java.simpleName)!=null -> {
                        viewDataBinding.bottomNav.selectedItemId = BottomNavItem.FEED.menuId
                    }
                    supportFragmentManager.findFragmentByTag(MapsFragment::class.java.simpleName)!=null -> {
                        viewDataBinding.bottomNav.selectedItemId = BottomNavItem.MAP.menuId
                    }
                }
            }
        }
        setupBottomNav(savedInstanceState)
    }

    private fun logInAndFeedWrite() {
        // 로그인 성공 했을 때 동작
        val onSignInSuccess: (UserInfoResponse) -> Unit = {
            startActivity(FeedWriteActivity.getIntent(this@MainActivity))
        }

        // 프로젝트에 저장된 토큰 없을 때
        if (!authViewModel.isUserSignIn()) {
            Timber.d("user not signIn")
            val kakaoSignInDialogFragment = KakaoSignInDialogFragment(
                onSignInSuccess = onSignInSuccess,
                onClose = {}
            )
            kakaoSignInDialogFragment.show(supportFragmentManager, kakaoSignInDialogFragment.tag)
        }

        // 프로젝트에 저장된 토큰 있을 때
        else {
            lifecycleScope.launchWhenCreated {
                ifAlreadySignIn(authViewModel, this@MainActivity)
            }
        }
        observeAuthViewModelUserInfo(this@MainActivity, authViewModel, onSignInSuccess)
    }


    private fun setupBottomNav(savedInstanceState: Bundle?) {
        viewDataBinding.bottomNav.itemIconTintList = null
        viewDataBinding.bottomNav.setOnNavigationItemSelectedListener { menuItem ->
            val navItem = BottomNavItem.values().find { bottomNavItem ->
                menuItem.itemId == bottomNavItem.menuId }
            navItem?.navigate?.invoke(navigationController)
            true
        }
        if (savedInstanceState == null) {
            BottomNavItem.HOME.navigate.invoke(navigationController)
            viewDataBinding.bottomNav.selectedItemId = R.id.home
        }
    }

    enum class BottomNavItem(
        @IdRes @MenuRes val menuId: Int,
        val navigate: NavigationController.() -> Unit
    ) {
        HOME(R.id.home, {
            navigateToHome()
        }),
        FEED(R.id.feed, {
            navigateToFeed()
        }),
        HIDDEN(R.id.hidden, {

        }),
        MAP(R.id.map, {
            navigateToMap()
        }),
        MY(R.id.my, {
            navigateToMy()
        })
    }
}
