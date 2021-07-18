package container.restaurant.android.util

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.button.MaterialButton
import container.restaurant.android.R
import container.restaurant.android.di.BASE_URL
import java.util.*

@BindingAdapter(value = ["android:imageUrl", "android:circleCrop"], requireAll = false)
fun ImageView.setImage(url: String?, circleCrop: Boolean = false) {
    if (url.isNullOrEmpty()) {
        return
    }

    Glide.with(context)
        .load(url)
        .apply {
            if (circleCrop) {
                centerCrop()
            }
        }
        .into(this)
}

@BindingAdapter("checker")
fun bindChecker(btn: MaterialButton, chk: Boolean?) {
    if(chk != null) {
        btn.isChecked = chk
    }
}

@BindingAdapter("html_text")
fun bindHtmlText(tv: TextView, htmlText: String?) {
    if(!htmlText.isNullOrEmpty()) {
        tv.text = CommUtils.fromHtml(htmlText)
    }
}

@BindingAdapter("user_level")
fun bindUserLevel(tv: TextView, userLevel: Int?) {
    if(userLevel != null) {
        when(userLevel) {
            in(0..1) -> tv.text = String.format("Lv%d. 텀블러", userLevel)
            2 -> tv.text = String.format("Lv%d. 밥그릇", userLevel)
            3 -> tv.text = String.format("Lv%d. 금 용기", userLevel)
            4 -> tv.text = String.format("Lv%d. 후라이팬", userLevel)
            5 -> tv.text = String.format("Lv%d. 냄비", userLevel)
            else -> tv.text = String.format("Lv%d. 냄비", userLevel)
        }
    }
}

@BindingAdapter("image_from_url")
fun bindImageFromUrl(iv: ImageView, url: String?) {
    if(!url.isNullOrEmpty()) {
        Glide.with(iv.context)
            .load(url)
            .error(R.drawable.img_level_up)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(iv)
    }
}

@BindingAdapter("favorite_feed_count")
fun bindFavoriteFeedCount(tv: TextView, count: Int?) {
    if(count != null) {
        tv.text = "피드 +$count"
    }
}

@BindingAdapter("bind:imageUrlWithOutBaseUrl")
fun ImageView.setImageUrlWithoutBaseUrl(url: String?){
    url?.let{
        val baseUrl = BASE_URL.substring(0, BASE_URL.lastIndex)
        val fullUrl = "$baseUrl$url"
        Glide.with(this)
            .load(fullUrl)
            .into(this)
    }
}

@BindingAdapter("bind:imageUrlWithBaseUrl")
fun ImageView.setImageUrlWithBaseUrl(url: String?){
    url?.let{
        Glide.with(this)
            .load(url)
            .into(this)
    }
}

@BindingAdapter("bind:emptyFeedBackgroundSetup")
fun ImageView.setEmptyFeedBackgroundSetup(boolean: String) {
    if(boolean.toBoolean()){
        when(Random().nextInt(3)){
            0 -> {
                setBackgroundResource(R.color.green_02)
            }
            1 -> {
                setBackgroundResource(R.color.blue_01)
            }
            2 -> {
                setBackgroundResource(R.color.yellow_02)
            }
        }
    }
}
