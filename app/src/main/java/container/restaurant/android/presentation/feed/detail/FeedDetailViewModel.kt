package container.restaurant.android.presentation.feed.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import container.restaurant.android.data.FeedCategory
import container.restaurant.android.data.repository.FeedDetailRepository
import container.restaurant.android.data.request.CommentReplyRequest
import container.restaurant.android.data.response.FeedDetailResponse
import container.restaurant.android.presentation.base.BaseViewModel
import container.restaurant.android.presentation.feed.item.CommentReplyItem
import container.restaurant.android.presentation.feed.item.ReplyItem
import container.restaurant.android.util.Event
import container.restaurant.android.util.RecyclerViewItemClickListeners
import container.restaurant.android.util.SingleLiveEvent
import container.restaurant.android.util.handleApiResponse
import kotlinx.coroutines.flow.collect

class FeedDetailViewModel(
    private val feedDetailRepository: FeedDetailRepository
): BaseViewModel()
    , RecyclerViewItemClickListeners.CommentReplyItemClickListener
    , RecyclerViewItemClickListeners.ReplyItemClickListener{

    val feedId = MutableLiveData<Int>()

    val commentReplyItemList = MutableLiveData<List<CommentReplyItem>>()

    private val _ownerNickname: MutableLiveData<String> = MutableLiveData()
    val ownerNickname: LiveData<String> = _ownerNickname

    private val _ownerContainerLevel: MutableLiveData<String> = MutableLiveData()
    val ownerContainerLevel: LiveData<String> = _ownerContainerLevel

    val likeCount = MutableLiveData<Int>()

    val scrapCount = MutableLiveData<Int>()

    private val _thumbnailUrl: MutableLiveData<String> = MutableLiveData()
    val thumbnailUrl: LiveData<String> = _thumbnailUrl

    private val _ownerProfileUrl: MutableLiveData<String> = MutableLiveData()
    val ownerProfileUrl: LiveData<String> = _ownerProfileUrl

    private val _content: MutableLiveData<String> = MutableLiveData()
    val content: LiveData<String> = _content

    private val _restaurantName: MutableLiveData<String> = MutableLiveData()
    val restaurantName: LiveData<String> = _restaurantName

    private val _categoryStr: MutableLiveData<String> = MutableLiveData()
    val categoryStr: LiveData<String> =_categoryStr

    private val _isWelcome: MutableLiveData<Boolean> = MutableLiveData()
    val isWelcome: LiveData<Boolean> =_isWelcome

    private val _difficulty: MutableLiveData<Int> = MutableLiveData()
    val difficulty: LiveData<Int> = _difficulty

    private val _mainMenuList: MutableLiveData<List<FeedDetailResponse.Menu>> = MutableLiveData()
    val mainMenuList: LiveData<List<FeedDetailResponse.Menu>> = _mainMenuList

    private val _subMenuList: MutableLiveData<List<FeedDetailResponse.Menu>> = MutableLiveData()
    val subMenuList: LiveData<List<FeedDetailResponse.Menu>> = _subMenuList

    val ownerProfileRes = MutableLiveData<Int>()

    val isLikeActivated = MutableLiveData<Boolean>()
    val isScrapActivated = MutableLiveData<Boolean>()

    suspend fun getFeedDetail(tokenBearer: String, feedId: Int) {
        feedDetailRepository.getFeedDetail(tokenBearer, feedId)
            .collect { response ->
                handleApiResponse(
                    response = response,
                    onSuccess = {
                        _ownerProfileUrl.value = it.data?.ownerProfile
                        _ownerNickname.value = it.data?.ownerNickname
                        _ownerContainerLevel.value = it.data?.ownerContainerLevel
                        _thumbnailUrl.value = it.data?.thumbnailUrl
                        _content.value = it.data?.content
                        _restaurantName.value = it.data?.restaurantName
                        _categoryStr.value = it.data?.category
                        _isWelcome.value = it.data?.isWelcome
                        _difficulty.value = it.data?.difficulty
                        _mainMenuList.value = it.data?.mainMenu
                        _subMenuList.value = it.data?.subMenu
                        likeCount.value = it.data?.likeCount
                        isLikeActivated.value = it.data?.isLike
                        scrapCount.value = it.data?.scrapCount
                        isScrapActivated.value = it.data?.isScrapped
                        it.data?.category?.let{ categoryEn ->
                            _categoryStr.value = FeedCategory.valueOf(categoryEn).menuKorean
                        }
                    }
                )
            }
    }

    suspend fun likeFeed(tokenBearer: String, feedId: Int, onLikeSuccess: () -> Unit, onLikeFail: () -> Unit) {
        feedDetailRepository.likeFeed(tokenBearer, feedId)
            .collect { response ->
                handleApiResponse(
                    response = response,
                    onSuccess = {
                        onLikeSuccess()
                    }, onException = {
                        onLikeFail()
                    }, onError = {
                        onLikeFail()
                    }
                )
            }
    }

    suspend fun cancelLikeFeed(tokenBearer: String, feedId: Int, onCancelSuccess: () -> Unit, onCancelFail: () -> Unit) {
        feedDetailRepository.cancelLikeFeed(tokenBearer, feedId)
            .collect { response ->
                handleApiResponse(
                    response = response,
                    onSuccess = {
                        onCancelSuccess()
                    }, onError = {
                        onCancelFail()
                    }, onException = {
                        onCancelFail()
                    }
                )
            }
    }

    suspend fun scrapFeed(tokenBearer: String, feedId: Int, onLikeSuccess: () -> Unit, onLikeFail: () -> Unit) {
        feedDetailRepository.scrapFeed(tokenBearer, feedId)
            .collect { response ->
                handleApiResponse(
                    response = response,
                    onSuccess = {
                        onLikeSuccess()
                    }, onException = {
                        onLikeFail()
                    }, onError = {
                        onLikeFail()
                    }
                )
            }
    }

    suspend fun cancelScrapFeed(tokenBearer: String, feedId: Int, onCancelSuccess: () -> Unit, onCancelFail: () -> Unit) {
        feedDetailRepository.cancelScrapFeed(tokenBearer, feedId)
            .collect { response ->
                handleApiResponse(
                    response = response,
                    onSuccess = {
                        onCancelSuccess()
                    }, onError = {
                        onCancelFail()
                    }, onException = {
                        onCancelFail()
                    }
                )
            }
    }

    suspend fun getFeedCommentReply(tokenBearer: String, feedId: Int, onGetSuccess: () -> Unit, onGetFail: () -> Unit) {
        feedDetailRepository.getFeedCommentReply(tokenBearer, feedId)
            .collect { response ->
                handleApiResponse(
                    response = response,
                    onSuccess = {
                        val commentReplyItemList = mutableListOf<CommentReplyItem>()
                        it.data?.dataWrapper?.commentReplyList?.forEach { commentReply ->
                            commentReplyItemList.add(CommentReplyItem(commentReply))
                        }
                        this.commentReplyItemList.value = commentReplyItemList
                        onGetSuccess()
                    }, onError = {
                        onGetFail()
                    }, onException = {
                        onGetFail()
                    }
                )
            }
    }

    suspend fun registerComment(
        tokenBearer: String,
        feedId: Int,
        commentReplyRequest: CommentReplyRequest,
        onPostSuccess: () -> Unit,
        onPostFail: () -> Unit
    ) {
        feedDetailRepository.registerCommentOrReply(tokenBearer, feedId, commentReplyRequest)
            .collect { response ->
                handleApiResponse(
                    response = response,
                    onSuccess = {
                        onPostSuccess()
                    }, onException = {
                        onPostFail()
                    }, onError = {
                        onPostFail()
                    }
                )
            }
    }

    override fun onCommentShowMoreClick(item: CommentReplyItem) {
        //TODO("Not yet implemented")
    }

    override fun onReplyShowMoreClick(item: ReplyItem) {
        //TODO("Not yet implemented")
    }

    private val _isLikeButtonClicked = SingleLiveEvent<Void>()
    val isLikeButtonClicked: LiveData<Void> = _isLikeButtonClicked

    fun onLikeButtonClick() {
        _isLikeButtonClicked.call()
    }

    private val _isScrapButtonClicked = SingleLiveEvent<Void>()
    val isScrapButtonClicked: LiveData<Void> = _isScrapButtonClicked

    fun onScrapButtonClick() {
        _isScrapButtonClicked.call()
    }

    private val _isBackButtonClicked: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val isBackButtonClicked: LiveData<Event<Boolean>> = _isBackButtonClicked

    fun onBackButtonClick() {
        _isBackButtonClicked.value = Event(true)
    }

    private val _isRegisterCommentOrReplyClicked = SingleLiveEvent<Void>()
    val isRegisterCommentOrReplyClicked: LiveData<Void> = _isRegisterCommentOrReplyClicked

    fun onRegisterCommentOrReplyClick() {
        _isRegisterCommentOrReplyClicked.call()
    }

}