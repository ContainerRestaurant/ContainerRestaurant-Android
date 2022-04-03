package container.restaurant.android.data.response

import com.google.gson.annotations.SerializedName

data class PostFeedResponse(
    @SerializedName("feedId") val feedId: Int,
    @SerializedName("levelUp") val levelUp: LevelUp?
) {
    data class LevelUp (
        @SerializedName("levelFeedCount") val levelFeedCnt: Int,
        @SerializedName("from") val lvFrom: String,
        @SerializedName("to") val lvTo: String
    )
}