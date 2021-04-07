package container.restaurant.android.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

internal class MainViewModel : ViewModel() {

    val navToFeed = MutableLiveData<Unit>()

    fun onClickNavToWriteFeed() {
        navToFeed.value = Unit
    }

    override fun onCleared() {
        super.onCleared()
    }
}