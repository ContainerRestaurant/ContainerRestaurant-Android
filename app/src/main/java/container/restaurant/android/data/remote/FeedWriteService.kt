package container.restaurant.android.data.remote

import com.skydoves.sandwich.ApiResponse
import container.restaurant.android.data.request.FeedWriteRequest
import container.restaurant.android.data.response.PostFeedResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface FeedWriteService {
    @POST("api/feed")
    suspend fun postFeed(
        @Header("Authorization") tokenBearer: String,
        @Body feedWriteRequest: FeedWriteRequest
    ): ApiResponse<PostFeedResponse>
}