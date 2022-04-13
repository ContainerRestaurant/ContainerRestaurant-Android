package container.restaurant.android.presentation.feed.item

import androidx.lifecycle.MutableLiveData
import container.restaurant.android.data.response.FeedListResponse

data class FeedPreviewItem(
    val feedPreviewDto: FeedListResponse.FeedPreviewDtoList.FeedPreviewDto,
    val isLike: MutableLiveData<Boolean>
)
