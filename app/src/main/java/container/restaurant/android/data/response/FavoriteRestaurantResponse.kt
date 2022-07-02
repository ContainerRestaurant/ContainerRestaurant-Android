package container.restaurant.android.data.response

import com.google.gson.annotations.SerializedName

data class FavoriteRestaurantResponse(
    @SerializedName("_embedded") val embedded: FavoriteRestaurantList
) {
    data class FavoriteRestaurantList(
        @SerializedName("restaurantFavoriteDtoList") val favoriteRestaurantList: List<FavoriteRestaurant>
    ) {
        data class FavoriteRestaurant(
            @SerializedName("id") val id: Int,
            @SerializedName("createDate") val createDate: String,
            @SerializedName("restaurant") val restaurant: Restaurant,
        ) {
            data class Restaurant(
                @SerializedName("id") val id: Int,
                @SerializedName("name") val name: String,
                @SerializedName("address") val address: String,
                @SerializedName("latitude") val latitude: Double,
                @SerializedName("longitude") val longitude: Double,
                @SerializedName("image_path") val imagePath: String,
                @SerializedName("feedCount") val feedCount: Int,
                @SerializedName("difficultyAvg") val difficultyAvg: Double,
                @SerializedName("isContainerFriendly") val isContainerFriendly: Boolean,
                @SerializedName("isFavorite") val isFavorite: Boolean,
                @SerializedName("bestMenu") val bestMenu: List<String>
            )
        }
    }
}