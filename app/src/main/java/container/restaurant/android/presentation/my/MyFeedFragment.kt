package container.restaurant.android.presentation.my

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import container.restaurant.android.R
import container.restaurant.android.databinding.FragmentMyFeedBinding
import container.restaurant.android.presentation.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyFeedFragment : BaseFragment<FragmentMyFeedBinding, MyViewModel>() {

    override val layoutResId: Int = R.layout.fragment_my_feed
    override val viewModel: MyViewModel by viewModel()

    private val args: MyFeedFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.viewModel = viewModel
        setBindItem()
        getMyFeedList()
    }

    private fun setBindItem() {
        viewDataBinding.apply {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun getMyFeedList() {
        lifecycleScope.launchWhenCreated {
            viewModel.getMyFeedList()
        }
    }
}