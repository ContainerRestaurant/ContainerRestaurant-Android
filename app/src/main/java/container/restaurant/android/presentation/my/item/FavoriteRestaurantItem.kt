package container.restaurant.android.presentation.my.item

import container.restaurant.android.data.response.FavoriteRestaurantResponse

data class FavoriteRestaurantItem(
    val favoriteRestaurant: FavoriteRestaurantResponse.FavoriteRestaurantList.FavoriteRestaurant
)