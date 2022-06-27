package container.restaurant.android.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2

class ContainerFragmentStateAdapter : FragmentStateAdapter {
    constructor(fragmentActivity: FragmentActivity) : super(fragmentActivity)
    constructor(fragment: Fragment) : super(fragment)
    constructor(fragmentManager: FragmentManager, lifecycle: Lifecycle) : super(fragmentManager, lifecycle)
    val fragments: ArrayList<Fragment> = arrayListOf()

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]

    fun addFragment(fragment: Fragment) {
        fragments.add(fragment)
        notifyItemInserted(fragments.size-1)
    }

    fun addFragments(listFragment: List<Fragment>){
        for(fragment in listFragment) {
            this.fragments.add(fragment)
            notifyItemInserted(fragments.size-1)
        }
    }

    fun removeFragment() {
        fragments.removeLast()
    }

    fun removeFragmentAfterScroll(viewPager2: ViewPager2) {
        fragments.removeLast()
        viewPager2.post {
            notifyItemRemoved(fragments.size)
        }
    }
}