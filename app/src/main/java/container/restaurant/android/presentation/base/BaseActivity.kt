package container.restaurant.android.presentation.base

import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import container.restaurant.android.R
import container.restaurant.android.dialog.ProgressDialog
import container.restaurant.android.dialog.AlertDialog
import container.restaurant.android.util.EventObserver

abstract class BaseActivity<T: ViewDataBinding, VM: BaseViewModel> : AppCompatActivity() {

    private lateinit var simpleDialog: AlertDialog
    private lateinit var loadingDialog: ProgressDialog

    lateinit var viewDataBinding: T

    abstract val layoutResId : Int
    abstract val viewModel : VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadingDialog = ProgressDialog(this)

        viewDataBinding = DataBindingUtil.setContentView(this, layoutResId)
        viewDataBinding.lifecycleOwner = this@BaseActivity

        observeData()
    }

    private fun observeData() {
        viewModel.toastMessage.observe(this, EventObserver {
            toastShort(it)
        })
    }

    fun toastShort(toastMessage: String) {
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()
    }

    fun toastShortOfFailMessage(themeKeyword: String) {
        toastShort(String.format(getString(R.string.failed_message), themeKeyword))
    }

    fun loadingCheck(isLoading:Boolean) {
        if(isLoading) showLoading() else hideLoading()
    }

    fun errorDialog(msg: String) {
        simpleDialog("Warning", msg, AlertDialog.WARNING_TYPE)
    }

    fun showLoading() {
        if(!ProgressDialog.showCheck)
            loadingDialog.show()
    }

    fun hideLoading() {
        if (ProgressDialog.showCheck)
            loadingDialog.dismiss()
    }

    fun simpleDialog(title: String, msg: String, type: Int) {
        simpleDialog = AlertDialog(context = this,titleStr = title,msgStr = msg,confirmStr = "Confirm", cancelStr = null, typeInt = type)

        simpleDialog.setSingleEventListener(object : AlertDialog.SingleEventListener {
            override fun confirmClick(dialogSelf: AlertDialog) {
                dialogSelf.dismiss()
            }
        })

        try {
            if(!AlertDialog.showCheck)
                simpleDialog.show()
        } catch (ex: Exception) {
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        }
    }

    // 바탕 클릭시 키보드 숨기기
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }
}