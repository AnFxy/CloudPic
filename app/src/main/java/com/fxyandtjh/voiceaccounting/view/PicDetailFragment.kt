package com.fxyandtjh.voiceaccounting.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.fxyandtjh.voiceaccounting.adapter.PicDetailPagerAdapter
import com.fxyandtjh.voiceaccounting.base.BaseFragment
import com.fxyandtjh.voiceaccounting.databinding.FragPicDetailBinding
import com.fxyandtjh.voiceaccounting.local.LocalCache
import com.fxyandtjh.voiceaccounting.net.response.PictureInfo
import com.fxyandtjh.voiceaccounting.tool.PicDividerUtil
import com.fxyandtjh.voiceaccounting.viewmodel.PicDetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PicDetailFragment : BaseFragment<PicDetailViewModel, FragPicDetailBinding>() {
    private val viewModel: PicDetailViewModel by viewModels()

    private val args: PicDetailFragmentArgs by navArgs()

    // 分组后的排序后的图片
    private val orderPicList: List<PictureInfo> by lazy {
        val orderGroup = PicDividerUtil.instance.dividerPicData(viewModel.picData.picList)
        val orderList = arrayListOf<PictureInfo>()
        orderGroup.forEach {
            orderList.addAll(it.second)
        }
        orderList
    }

    private val pagerAdapter: PicDetailPagerAdapter by lazy {
        args.picDetailArgs?.let {
            viewModel.picData = it
        }
        PicDetailPagerAdapter(
            fragment = this@PicDetailFragment,
            dataPics = orderPicList.map { it.imageUrl }
        )
    }

    override fun getViewMode(): PicDetailViewModel = viewModel

    override fun getViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): FragPicDetailBinding = FragPicDetailBinding.inflate(inflater, parent, false)

    override fun setLayout() {
        binding.vpPic.adapter = pagerAdapter
        val targetIndex = viewModel.picData.selectedIndex
        if (targetIndex != -1) {
            updateTitleContent(selectedIndex = targetIndex, isInit = true)
        }
    }

    override fun setObserver() {
        binding.vpPic.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateTitleContent(position)
            }
        })
    }

    private fun updateTitleContent(selectedIndex: Int, isInit: Boolean = false) {
        val listSize = orderPicList.size
        if (isInit) {
            val targetPictureInfo = viewModel.picData.picList[selectedIndex]
            val targetSelectedIndex = orderPicList.indexOf(targetPictureInfo)
            binding.vpPic.setCurrentItem(targetSelectedIndex, false)
            binding.tvTitle.text =
                "${LocalCache.currentAlbum.title}(${targetSelectedIndex + 1}/${listSize})"
        } else {
            binding.tvTitle.text =
                "${LocalCache.currentAlbum.title}(${selectedIndex + 1}/${listSize})"
        }
    }
}