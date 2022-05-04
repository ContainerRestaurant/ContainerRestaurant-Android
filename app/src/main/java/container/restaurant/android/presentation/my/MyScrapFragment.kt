package container.restaurant.android.presentation.my

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import container.restaurant.android.R
import container.restaurant.android.databinding.FragmentMyScrapBinding
import container.restaurant.android.presentation.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyScrapFragment : BaseFragment<FragmentMyScrapBinding, MyViewModel>() {

    override val layoutResId: Int = R.layout.fragment_my_scrap
    override val viewModel: MyViewModel by viewModel()

    private val args: MyScrapFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.viewModel = viewModel
        setBindItem()
        getMyScrapFeedList()
    }

    private fun setBindItem() {
        viewDataBinding.apply {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun getMyScrapFeedList(){
        lifecycleScope.launchWhenCreated {
            viewModel.getMyScrapFeedList()
        }
    }
}