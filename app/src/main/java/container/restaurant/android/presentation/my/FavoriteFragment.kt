package container.restaurant.android.presentation.my

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import container.restaurant.android.R
import container.restaurant.android.databinding.FragmentFavoriteBinding
import container.restaurant.android.presentation.base.BaseFragment
import container.restaurant.android.util.observe
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteFragment : BaseFragment<FragmentFavoriteBinding, MyViewModel>() {

    override val layoutResId: Int = R.layout.fragment_favorite
    override val viewModel: MyViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.viewModel = viewModel

        setBindItem()
        subscribeUi()
    }

    private fun setBindItem() {
        viewDataBinding.apply {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun subscribeUi() {
        with(viewModel) {
            observe(viewLoading, ::loadingCheck)
            observe(getErrorMsg, ::errorDialog)
        }
    }
}