package com.fxyandtjh.voiceaccounting.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.fxyandtjh.voiceaccounting.base.Constants
import com.fxyandtjh.voiceaccounting.view.SinglePicFragment

class PicDetailPagerAdapter(
    fragment: Fragment,
    private val dataPics: List<String>
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = dataPics.size

    override fun createFragment(position: Int): Fragment {
        val fragment = SinglePicFragment()
        fragment.arguments = Bundle().apply {
            putString(Constants.PIC_URL, dataPics[position])
        }
        return fragment
    }
}
