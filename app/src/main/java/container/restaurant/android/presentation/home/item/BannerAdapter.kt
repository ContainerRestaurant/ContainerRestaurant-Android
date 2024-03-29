package container.restaurant.android.presentation.home.item

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import container.restaurant.android.R
import container.restaurant.android.data.response.HomeInfoResponse.Banner
import container.restaurant.android.databinding.ItemBannerBinding

internal class BannerAdapter : RecyclerView.Adapter<BannerViewHolder>() {

    private val items = mutableListOf<Banner>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val binding = DataBindingUtil.inflate<ItemBannerBinding>(LayoutInflater.from(parent.context), R.layout.item_banner, parent, false)
        return BannerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun addItems(items: List<Banner>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }
}

class BannerViewHolder(
    private val binding: ItemBannerBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(bannerContent: Banner) {
        binding.item = bannerContent
    }
}
