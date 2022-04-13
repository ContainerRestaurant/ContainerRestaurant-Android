package container.restaurant.android.util

import container.restaurant.android.data.CategorySelection
import container.restaurant.android.data.response.SearchLocationResponse
import container.restaurant.android.presentation.feed.item.FeedPreviewItem

interface RecyclerViewItemClickListeners {
    interface CategorySelectionItemClickListener {
        fun onCategorySelectionItemClick(item: CategorySelection, adapterPosition: Int)
    }
    interface FoodPhotoItemClickListener {
        fun onDeleteClick(adapterPosition: Int)
    }
    interface SearchResultItemClickListener {
        fun onSearchResultItemClick(item: SearchLocationResponse.Item)
    }
    interface SortingCategoryItemClickListener {
        fun onSortingCategoryItemClick(adapterPosition: Int)
    }
    interface ExploreFeedItemClickListener {
        fun onExploreFeedItemClick(feedId: Int)
        fun onLikeFeedItemClick(feedPreviewItem: FeedPreviewItem)
    }
}