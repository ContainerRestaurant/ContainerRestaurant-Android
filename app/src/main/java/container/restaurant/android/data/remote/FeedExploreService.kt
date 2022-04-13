package container.restaurant.android.data.remote

import com.skydoves.sandwich.ApiResponse
import container.restaurant.android.data.response.FeedListResponse
import container.restaurant.android.data.response.FeedResponse
import retrofit2.http.*

interface FeedExploreService {

    @GET("api/feed")
    suspend fun fetchFeeds(
        @Query("category") category: String? = null,
        @Query("sort") sort: String? = null,
        @Query("page") page: Int,
        @Query("size") perPage: Int
    ): FeedResponse

    @GET("api/feed/restaurant/{restaurantId}")
    suspend fun fetchResFeed(
        @Path("restaurantId") resId : Long
    ): FeedResponse


    @GET("api/feed")
    suspend fun feedList(
        @Query("category") category: String? = null,
        @Query("sort") sort: String? = null,
        @Query("page") page: Int,
        @Query("size") perPage: Int
    ): ApiResponse<FeedListResponse>

    @POST("/api/like/feed/{feedId}")
    suspend fun likeFeed(
        @Path("feedId") feedId: Int
    ): ApiResponse<Void>

    @DELETE("/api/like/feed/{feedId}")
    suspend fun cancelLikeFeed(
        @Path("feedId") feedId: Int
    ): ApiResponse<Void>
}