package container.restaurant.android.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import container.restaurant.android.presentation.base.BaseViewModel
import container.restaurant.android.util.SingleLiveEvent

internal class MainViewModel : BaseViewModel() {

    private val _isFeedWriteClicked = SingleLiveEvent<Void>()
    val isFeedWriteClicked:LiveData<Void> = _isFeedWriteClicked

    fun onFeedWriteClick() {
        _isFeedWriteClicked.call()
    }

    override fun onCleared() {
        super.onCleared()
    }
}