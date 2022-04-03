package container.restaurant.android.data.request

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class FeedWriteRequest(
    @SerializedName("restaurantCreateDto") val restaurantCreateDto: RestaurantCreateDto,
    @SerializedName("category") val category: String,
    @SerializedName("difficulty") val difficulty: Int,
    @SerializedName("welcome") val welcome: Boolean,
    @SerializedName("thumbnailImageId") val thumbnailImageId: Int,
    @SerializedName("content") val content: String,
    @SerializedName("mainMenu") val mainMenu: List<MainMenu>,
    @SerializedName("subMenu") val subMenu: List<SubMenu>
) {
    data class RestaurantCreateDto(
        @SerializedName("name") val name: String,
        @SerializedName("address") val address: String,
        @SerializedName("latitude") val latitude: Double,
        @SerializedName("longitude") val longitude: Double
    )

    data class MainMenu(
        @SerializedName("menuName") val menuName: String,
        @SerializedName("container") val container: String
    )

    data class SubMenu(
        @SerializedName("menuName") val menuName: String,
        @SerializedName("container") val container: String
    )
}