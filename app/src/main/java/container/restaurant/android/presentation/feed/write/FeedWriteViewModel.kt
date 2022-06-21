package container.restaurant.android.presentation.feed.write

import androidx.lifecycle.*
import com.naver.maps.geometry.Tm128
import container.restaurant.android.data.*
import container.restaurant.android.data.repository.FeedWriteRepository
import container.restaurant.android.data.request.FeedWriteRequest
import container.restaurant.android.data.response.SearchLocationResponse
import container.restaurant.android.presentation.base.BaseViewModel
import container.restaurant.android.util.*
import kotlinx.coroutines.flow.collect
import timber.log.Timber

internal class FeedWriteViewModel(private val feedWriteRepository: FeedWriteRepository) : BaseViewModel(),
    RecyclerViewItemClickListeners.CategorySelectionItemClickListener,
    RecyclerViewItemClickListeners.FoodPhotoItemClickListener,
    RecyclerViewItemClickListeners.SearchResultItemClickListener{
    val getErrorMsg = MutableLiveData<String>()
    val viewLoading = MutableLiveData<Boolean>()

    private val _mainMenuList: MutableLiveData<MutableList<MainMenu>> = MutableLiveData(
        mutableListOf(MainMenu())
    )
    val mainMenuList: LiveData<MutableList<MainMenu>> = _mainMenuList

    private val _subMenuList: MutableLiveData<MutableList<SubMenu>> = MutableLiveData(
        mutableListOf(SubMenu())
    )
    val subMenuList: LiveData<MutableList<SubMenu>> = _subMenuList

    val foodPhotoList: MutableLiveData<MutableList<FoodPhoto>> = MutableLiveData(
        mutableListOf()
    )

    private val _searchLocationList =
        MutableLiveData<List<SearchLocationResponse.Item>>()
    val searchLocationList: LiveData<List<SearchLocationResponse.Item>> = _searchLocationList

    // 식당 검색 결과 선택 시 바뀌는 아이템
    private val _selectedLocationItem = MutableLiveData<FeedWriteRequest.RestaurantCreateDto?>()
    val selectedLocationItem: LiveData<FeedWriteRequest.RestaurantCreateDto?> = _selectedLocationItem

    // 검색창에 입력이 일어날 때마다 바뀌는 이름
    val searchPlaceName: MutableLiveData<String> = MutableLiveData()

    val isWelcomed = MutableLiveData<Boolean>(false)

    val difficulty = MutableLiveData<Int>()

    val categoryList = mutableListOf(
        CategorySelection(FoodCategory.KOREAN),
        CategorySelection(FoodCategory.NIGHT_MEAL),
        CategorySelection(FoodCategory.CHINESE),
        CategorySelection(FoodCategory.SCHOOL_FOOD),
        CategorySelection(FoodCategory.FAST_FOOD),
        CategorySelection(FoodCategory.ASIAN_AND_WESTERN),
        CategorySelection(FoodCategory.COFFEE_AND_DESSERT),
        CategorySelection(FoodCategory.JAPANESE),
        CategorySelection(FoodCategory.CHICKEN_AND_PIZZA)
    )

    // 사용자가 선택한 음식 카테고리를 저장하는 변수. 선택되지 않았으면 null임
    var selectedFoodCategory: FoodCategory? = null

    val content = MutableLiveData<String>()

    fun erasePlaceName() {
        _selectedLocationItem.value = null
    }

    fun makeSearchResultEmpty() {
        _searchLocationList.value = listOf()
    }

    suspend fun getSearchPlace(placeName: String) {
        feedWriteRepository.getSearchLocation(placeName)
            .collect { response ->
                handleApiResponse(
                    response = response,
                    onSuccess = {
                        _searchLocationList.value = it.data?.items
                        Timber.d("it.data.items : ${searchLocationList.value}")
                    },
                    onError = {
                        Timber.d("it.errorBody : ${it.errorBody}")
                        Timber.d("it.headers : ${it.headers}")
                        Timber.d("it.raw : ${it.raw}")
                        Timber.d("it.response : ${it.response}")
                        Timber.d("it.statusCode : ${it.statusCode}")
                    },
                    onException = {
                        Timber.d("it.message : ${it.message}")
                        Timber.d("it.exception : ${it.exception}")
                    }
                )
            }
    }

    suspend fun postFeed(tokenBearer: String, feedWriteRequest: FeedWriteRequest) {
        feedWriteRepository.postFeed(tokenBearer, feedWriteRequest)
            .collect { response ->
                handleApiResponse(
                    response = response,
                    onSuccess = {

                    },
                    onError = {

                    },
                    onException = {

                    }
                )
            }
    }

    // 클릭 이벤트들
    private val _isAddPhotoButtonClicked: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val isAddPhotoButtonClicked: LiveData<Event<Boolean>> = _isAddPhotoButtonClicked

    fun onAddPhotoButtonClick() {
        _isAddPhotoButtonClicked.value = Event(true)
    }

    private val _isBackButtonClicked: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val isBackButtonClicked: LiveData<Event<Boolean>> = _isBackButtonClicked

    fun onBackButtonClick() {
        _isBackButtonClicked.value = Event(true)
    }

    fun onWelcomedButtonClick() {
        isWelcomed.value = isWelcomed.value?.not()
    }

    private val _isCloseSearchButtonClicked: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val isCloseSearchButtonClicked: LiveData<Event<Boolean>> = _isCloseSearchButtonClicked

    fun onCloseSearchButtonClick() {
        _isCloseSearchButtonClicked.value = Event(true)
    }

    private val _isSearchButtonClicked: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val isSearchButtonClicked: LiveData<Event<Boolean>> = _isSearchButtonClicked

    fun onSearchButtonClick() {
        _isSearchButtonClicked.value = Event(true)
    }

    private val _isSearchEditTextClicked: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val isSearchEditTextClicked: LiveData<Event<Boolean>> = _isSearchEditTextClicked

    fun onSearchEditTextClick() {
        _isSearchEditTextClicked.value = Event(true)
    }

    private val _isSearchResultItemClicked: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val isSearchResultItemClicked: LiveData<Event<Boolean>> = _isSearchResultItemClicked

    override fun onSearchResultItemClick(item: SearchLocationResponse.Item) {
        val latLng = Tm128(item.mapx.toDouble(), item.mapy.toDouble()).toLatLng()
        _selectedLocationItem.value = FeedWriteRequest.RestaurantCreateDto(
            item.title,
            item.address,
            latLng.latitude,
            latLng.longitude
        )
        _isSearchResultItemClicked.value = Event(true)
    }

    private val _isErasePlaceNameClicked: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val isErasePlaceNameClicked: LiveData<Event<Boolean>> = _isErasePlaceNameClicked

    fun onErasePlaceNameClick() {
        _isErasePlaceNameClicked.value = Event(true)
    }

    private val _isEraseSearchPlaceNameClicked: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val isEraseSearchPlaceNameClicked: LiveData<Event<Boolean>> = _isEraseSearchPlaceNameClicked

    fun onEraseSearchPlaceNameClick() {
        _isEraseSearchPlaceNameClicked.value = Event(true)
    }

    private val _isCompleteClicked = SingleLiveEvent<Void>()
    val isCompleteClicked: LiveData<Void> = _isCompleteClicked

    fun onCompleteClick() {
        _isCompleteClicked.call()
    }

    override fun onDeleteClick(adapterPosition: Int) {
        foodPhotoList.value?.removeAt(adapterPosition)
        foodPhotoList.value = foodPhotoList.value
    }

    override fun onCategorySelectionItemClick(item: CategorySelection, adapterPosition: Int) {
        Timber.d("$item, $adapterPosition")
        if (item.checked.value != null) {
            //이미 체크가 되어있다면 체크 해제
            if (item.checked.value!!) {
                item.checked.value = false
                selectedFoodCategory = null
            }
            //그렇지 않다면 다른 것이 선택되있거나 아무것도 선택되지 않은 상태 => 지금 아이템만 체크 상태로 변경
            else {
                categoryList.forEachIndexed { index, categorySelection ->
                    categorySelection.checked.value = (index == adapterPosition)
                    if (index == adapterPosition) selectedFoodCategory = categorySelection.foodCategory
                }
            }
        }
    }

    fun onAddMainMenuButtonClick() {
        _mainMenuList.value?.add(MainMenu())
        _mainMenuList.value = _mainMenuList.value
    }

    fun onAddSubMenuButtonClick() {
        _subMenuList.value?.add(SubMenu())
        _subMenuList.value = _subMenuList.value
    }
}