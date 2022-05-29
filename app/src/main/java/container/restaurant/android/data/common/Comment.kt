package container.restaurant.android.data.common

import com.google.gson.annotations.SerializedName

data class CommentReply(
    @SerializedName("id") val commentReplyId: Int?,
    @SerializedName("content") val content: String?,
    @SerializedName("isDeleted") val isDeleted: Boolean?,
    @SerializedName("ownerId") val ownerId: Int?,
    @SerializedName("ownerNickName") val ownerNickname: String?,
    @SerializedName("ownerProfile") val ownerProfileUrl: String?,
    @SerializedName("ownerLevelTitle") val ownerLevelTitle: String?,
    @SerializedName("createdDate") val createdDate: String?,
    @SerializedName("isLike") val isLike: Boolean?,
    @SerializedName("commentReply") val replyList: List<CommentReply>
)
