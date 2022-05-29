package container.restaurant.android.data.response

import com.google.gson.annotations.SerializedName
import container.restaurant.android.data.common.CommentReply

data class FeedCommentReplyResponse(
    @SerializedName("_embedded") val dataWrapper: DataWrapper
) {
    data class DataWrapper(
        @SerializedName("commentInfoDtoList") val commentReplyList: List<CommentReply>
    )
}
