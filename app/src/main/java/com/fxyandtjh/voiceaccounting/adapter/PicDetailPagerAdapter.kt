package com.fxyandtjh.voiceaccounting.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.fxyandtjh.voiceaccounting.view.SinglePicFragment

class PicDetailPagerAdapter(
    fragment: Fragment,
    private val itemClickCallBack: () -> Unit
) : FragmentStateAdapter(fragment) {

    var mFragments: List<SinglePicFragment> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = mFragments.size

    override fun createFragment(position: Int): Fragment {
        val fragment = mFragments[position]
        fragment.clickViewCallBack = {
            itemClickCallBack.invoke()
        }
        return fragment
    }
}
