package container.restaurant.android.presentation.feed.category

import androidx.lifecycle.*
import container.restaurant.android.data.FeedCategory
import container.restaurant.android.data.SortingCategory
import container.restaurant.android.data.repository.FeedExploreRepository
import container.restaurant.android.data.response.FeedListResponse
import container.restaurant.android.presentation.feed.item.FeedPreviewItem
import container.restaurant.android.util.Event
import container.restaurant.android.util.RecyclerViewItemClickListeners
import container.restaurant.android.util.SingleLiveEvent
import container.restaurant.android.util.handleApiResponse
import kotlinx.coroutines.flow.collect
import timber.log.Timber

internal class FeedCategoryViewModel(
    private val feedExploreRepository: FeedExploreRepository,
    private val feedCategory: FeedCategory
) : ViewModel(),
    RecyclerViewItemClickListeners.ExploreFeedItemClickListener {

    private val _feedList: MutableLiveData<List<FeedPreviewItem>> =
        MutableLiveData()
    val feedList: LiveData<List<FeedPreviewItem>> = _feedList

    var selectedFeedId: Int = -1

    suspend fun getFeedList(sortingCategory: SortingCategory) {
        val categoryName =
            if (feedCategory.name == FeedCategory.ALL.name) null
            else feedCategory.name
        feedExploreRepository.getFeedList(categoryName, sortingCategory, 0)
            .collect { response ->
                handleApiResponse(
                    response = response,
                    onSuccess = {
                        val tempFeedItemList = mutableListOf<FeedPreviewItem>()
                            .apply {
                                it.data?.embedded?.feedPreviewDtoList?.forEach { feedPreviewDto ->
                                    add(FeedPreviewItem(feedPreviewDto, MutableLiveData(feedPreviewDto.isLike)))
                                }
                            }
                        _feedList.value = tempFeedItemList
                        Timber.d("feedList.value = ${feedList.value}")
                    }
                )
            }
    }


    private val _isExploreFeedItemClicked: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val isExploreFeedItemClicked: LiveData<Event<Boolean>> = _isExploreFeedItemClicked

    override fun onExploreFeedItemClick(feedId: Int) {
        selectedFeedId = feedId
        Timber.d("onExploreFeedItemClick, selectedFeedId : $selectedFeedId")
        _isExploreFeedItemClicked.value = Event(true)
    }

    private val _isLikeFeedItemClicked = SingleLiveEvent<FeedPreviewItem>()
    val isLikeFeedItemClicked: LiveData<FeedPreviewItem> = _isLikeFeedItemClicked

    override fun onLikeFeedItemClick(feedPreviewItem: FeedPreviewItem) {
        _isLikeFeedItemClicked.value = feedPreviewItem
    }
    /** 여기부터는 원래 있던 코드 **/
//    private val feedResponse = MutableLiveData<FeedResponse>()
//    private var feedSort: SortingCategory = SortingCategory.LATEST
//
//    val feeds = feedResponse.map {
//        it.feedModel?.feeds ?: emptyList()
//    }
//    val lastPage = feedResponse.map {
//        it.page.totalPage
//    }
//    val isRefreshing = MutableLiveData<Boolean>()
//    val errorToast = SingleLiveEvent<String>()
//    val loading = MutableLiveData<Boolean>()
//
//    init {
//        fetchFeedsByCategory(FeedCategoryFragment.page)
//    }
//
//    fun onRefresh() {
//        isRefreshing.value = false
//    }
//
//    fun fetchMore(page: Int) {
//        fetchFeedsByCategory(page)
//    }
//
//    private fun fetchFeedsByCategory(page: Int) {
//        viewModelScope.launch {
//            loading.value = true
//            val feedsResult =
//                feedExploreRepository.fetchFeedsWithCategory(getFeedCategory(), feedSort, page)
//            when (feedsResult) {
//                is ResultState.Success -> feedResponse.value = feedsResult.data ?: return@launch
//                is ResultState.Error -> errorToast.value = feedsResult.error?.errorMessage ?: ""
//            }
//            loading.value = false
//        }
//    }
//
//    private fun getFeedCategory() = feedCategory?.name ?: ""
}