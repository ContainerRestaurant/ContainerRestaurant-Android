package container.restaurant.android.data.request

import com.google.gson.annotations.SerializedName

data class CommentReplyRequest(
    @SerializedName("content") val content: String,
    @SerializedName("upperReplyId") val upperReplyId: Int? = null
)
