package container.restaurant.android.di

import androidx.appcompat.app.AppCompatActivity
import container.restaurant.android.presentation.MainViewModel
import container.restaurant.android.presentation.NavigationController
import container.restaurant.android.presentation.auth.AuthViewModel
import container.restaurant.android.presentation.feed.explore.FeedExploreViewModel
import container.restaurant.android.presentation.feed.all.FeedAllViewModel
import container.restaurant.android.presentation.feed.category.FeedCategoryViewModel
import container.restaurant.android.presentation.feed.detail.FeedDetailViewModel
import container.restaurant.android.presentation.feed.write.FeedWriteViewModel
import container.restaurant.android.presentation.home.HomeViewModel
import container.restaurant.android.presentation.map.item.FeedRestaurantViewModel
import container.restaurant.android.presentation.map.item.MapsViewModel
import container.restaurant.android.presentation.my.MyViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val presentationModule = module {
    single { (activity: AppCompatActivity) -> NavigationController(activity) }
    viewModel { MainViewModel() }
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { FeedExploreViewModel() }
    viewModel { FeedCategoryViewModel(get(), it[0]) }
    viewModel { FeedAllViewModel(get()) }
    viewModel { FeedWriteViewModel(get()) }
    viewModel { MyViewModel(get(), get(), get()) }
    viewModel { MapsViewModel(get()) }
    viewModel { FeedRestaurantViewModel(get()) }
    viewModel { AuthViewModel(get(), get()) }
    viewModel { FeedDetailViewModel(get()) }
}