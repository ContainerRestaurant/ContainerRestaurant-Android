package container.restaurant.android.util

import android.content.Context
import android.widget.Toast
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenCreated
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.suspendOnError
import com.skydoves.sandwich.suspendOnException
import com.skydoves.sandwich.suspendOnSuccess
import container.restaurant.android.R
import container.restaurant.android.presentation.base.BaseFragment
import container.restaurant.android.presentation.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

fun setUserProfileResByLevelTitle(context: Context, userProfileRes: MutableLiveData<Int>, userLevelTitle: String?) {
    when(userLevelTitle) {
        context.getString(R.string.empty_profile_lv1) -> userProfileRes.value =
            R.drawable.empty_profile_lv1
        context.getString(R.string.empty_profile_lv2) -> userProfileRes.value =
            R.drawable.empty_profile_lv2
        context.getString(R.string.empty_profile_lv3) -> userProfileRes.value =
            R.drawable.empty_profile_lv3
        context.getString(R.string.empty_profile_lv4) -> userProfileRes.value =
            R.drawable.empty_profile_lv4
        context.getString(R.string.empty_profile_lv5) -> userProfileRes.value =
            R.drawable.empty_profile_lv5
        else -> userProfileRes.value =
            R.drawable.empty_profile_lv1
    }
}

fun setHomeIconByLevelTitle(context: Context, homeIconResByUserLevel: MutableLiveData<Int>, userLevelTitle: String?) {
    when(userLevelTitle) {
        context.getString(R.string.empty_profile_lv1) -> homeIconResByUserLevel.value =
            R.drawable.ic_home_lv1
        context.getString(R.string.empty_profile_lv2) -> homeIconResByUserLevel.value =
            R.drawable.ic_home_lv2
        context.getString(R.string.empty_profile_lv3) -> homeIconResByUserLevel.value =
            R.drawable.ic_home_lv3
        context.getString(R.string.empty_profile_lv4) -> homeIconResByUserLevel.value =
            R.drawable.ic_home_lv4
        context.getString(R.string.empty_profile_lv5) -> homeIconResByUserLevel.value =
            R.drawable.ic_home_lv5
        else -> homeIconResByUserLevel.value =
            R.drawable.ic_home_lv1
    }
}

fun <T> flowApiResponse(response: ApiResponse<T>): Flow<ApiResponse<T>> =
    flow {
        response
            .suspendOnSuccess {
                emit(this)
            }
            .suspendOnError {
                emit(this)
            }
            .suspendOnException {
                emit(this)
            }
    }.flowOn(Dispatchers.IO)

fun toastShort(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun toastLong(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}

fun toastShortOfFailMessage(context: Context, message: String) {
    val failFullMessage = context.getString(R.string.failed_message, message)
    toastShort(context, failFullMessage)
}

fun toastLongOfFailMessage(context: Context, message: String) {
    val failFullMessage = context.getString(R.string.failed_message, message)
    toastLong(context, failFullMessage)
}

/** 프래그먼트가 생성된 후 데이터를 관찰하기 위함(ex. viewpager2 사용시)
 *
 * @param fragment 해당 BaseFragment(뷰모델을 포함하고 있음)
 * @param fragmentActivity 액티비티
 * @param observeWithViewModel 옵저빙을 실행할 내용
 */
fun <T : BaseFragment<out ViewDataBinding, out R>, R : BaseViewModel>
        observeFragmentData(
    fragment: T,
    fragmentActivity: FragmentActivity,
    observeWithViewModel: R.() -> Unit
) {
    fragmentActivity.lifecycleScope.launchWhenCreated {
        fragment.whenCreated {
            observeWithViewModel(fragment.viewModel)
        }
    }
}
