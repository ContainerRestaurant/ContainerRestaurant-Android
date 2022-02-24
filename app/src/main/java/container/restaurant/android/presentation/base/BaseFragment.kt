package container.restaurant.android.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import container.restaurant.android.R
import container.restaurant.android.dialog.AlertDialog
import container.restaurant.android.util.EventObserver

abstract class BaseFragment<T: ViewDataBinding, VM: BaseViewModel> : Fragment()  {
    lateinit var viewDataBinding: T

    abstract val layoutResId : Int
    abstract val viewModel : VM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = DataBindingUtil.inflate(inflater, layoutResId, container, false)
        // 라이브 데이터 업데이트를 위한 설정
        viewDataBinding.lifecycleOwner = viewLifecycleOwner

        observeData()

        return viewDataBinding.root
    }
    private fun observeData() {
        //토스트 메세지 띄우기
        viewModel.toastMessage.observe(viewLifecycleOwner, EventObserver {
            toastShort(it)
        })
    }

    fun toastShort(str:String) {
        Toast.makeText(requireContext(), str, Toast.LENGTH_SHORT).show()
    }

    fun toastShortOfFailMessage(themeKeyword: String) {
        toastShort(String.format(getString(R.string.failed_message),themeKeyword))
    }

    fun simpleDialog(title: String, msg: String, type: Int) {
        (requireActivity() as BaseActivity<*, *>).simpleDialog(title, msg, type)
    }

    fun loadingCheck(isLoading:Boolean) {
        if(isLoading) showLoading() else hideLoading()
    }

    fun showLoading() {
        (requireActivity() as BaseActivity<*, *>).showLoading()
    }
    fun hideLoading() {
        (requireActivity() as BaseActivity<*, *>).hideLoading()
    }

    fun errorDialog(msg: String) {
        simpleDialog("Warning", msg, AlertDialog.WARNING_TYPE)
    }
}