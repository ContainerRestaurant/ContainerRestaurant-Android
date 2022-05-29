package container.restaurant.android.data.remote

import com.skydoves.sandwich.ApiResponse
import container.restaurant.android.data.response.FeedCommentReplyResponse
import container.restaurant.android.data.response.FeedDetailResponse
import retrofit2.http.*

interface FeedDetailService {
    @GET("api/feed/{feed_id}")
    suspend fun feedDetail(@Header("Authorization") tokenBearer: String, @Path("feed_id") feedId: Int): ApiResponse<FeedDetailResponse>

    @POST("/api/scrap/{feedId}")
    suspend fun scrapFeed(
        @Header("Authorization") tokenBearer: String?,
        @Path("feedId") feedId: Int
    ): ApiResponse<Void>

    @DELETE("/api/scrap/{feedId}")
    suspend fun cancelScrapFeed(
        @Header("Authorization") tokenBearer: String?,
        @Path("feedId") feedId: Int
    ): ApiResponse<Void>

    @GET("/api/comment/feed/{feedId}")
    suspend fun getFeedCommentReply(@Header("Authorization") tokenBearer: String, @Path("feedId") feedId: Int): ApiResponse<FeedCommentReplyResponse>
}