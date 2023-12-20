package com.fxyandtjh.voiceaccounting.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ToastUtils
import com.fxyandtjh.voiceaccounting.R
import com.fxyandtjh.voiceaccounting.adapter.PicDetailPagerAdapter
import com.fxyandtjh.voiceaccounting.base.BaseFragment
import com.fxyandtjh.voiceaccounting.base.Constants
import com.fxyandtjh.voiceaccounting.base.RxDialogSet
import com.fxyandtjh.voiceaccounting.base.setLimitClickListener
import com.fxyandtjh.voiceaccounting.databinding.FragPicDetailBinding
import com.fxyandtjh.voiceaccounting.local.LocalCache
import com.fxyandtjh.voiceaccounting.net.response.PictureInfo
import com.fxyandtjh.voiceaccounting.tool.PicDividerUtil
import com.fxyandtjh.voiceaccounting.tool.SimplePermissionCallBack
import com.fxyandtjh.voiceaccounting.tool.setVisible
import com.fxyandtjh.voiceaccounting.tool.setVisibleWithUnVisual
import com.fxyandtjh.voiceaccounting.viewmodel.PicDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PicDetailFragment : BaseFragment<PicDetailViewModel, FragPicDetailBinding>() {
    private val viewModel: PicDetailViewModel by viewModels()

    private val args: PicDetailFragmentArgs by navArgs()

    private val moreDialog: RxDialogSet? by lazy {
        context?.let {
            val tempDialog = RxDialogSet(it, R.style.SimpleDialog, R.layout.dia_more)
            tempDialog.setViewState<ImageView>(R.id.btn_close) {
                setLimitClickListener {
                    tempDialog.dismiss()
                    binding.btnMore.setVisibleWithUnVisual(true)
                }
            }
            tempDialog.setViewState<LinearLayout>(R.id.btn_exchange) {
                setLimitClickListener {
                    // 操作对应的fragment的imageView进行旋转
                    try {
                        val targetFragment =
                            pagerAdapter.mFragments[viewModel.picData.selectedIndex]
                        targetFragment.rotateImage()
                    } catch (e: Exception) {
                        ToastUtils.showShort(getText(R.string.error_rotate))
                    }
                }
            }
            tempDialog.setViewState<LinearLayout>(R.id.btn_download) {
                setLimitClickListener {
                    // 调用存储权限
                    PermissionUtils.permission(
                        PermissionConstants.STORAGE
                    ).callback(SimplePermissionCallBack {
                        // 下载对应的图片
                        try {
                            val targetUrl = orderPicList[viewModel.picData.selectedIndex]
                            viewModel.downloadPicture(targetUrl.imageUrl)
                        } catch (e: Exception) {
                            ToastUtils.showShort(getText(R.string.error_download))
                        }
                    }).request()
                }
            }
            tempDialog
        }
    }

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
        val tempAdapter = PicDetailPagerAdapter(
            fragment = this@PicDetailFragment
        ) {
            updateTitleStatus()
        }
        tempAdapter.mFragments = orderPicList.map { item ->
            val tempFragment = SinglePicFragment()
            tempFragment.arguments = Bundle().apply {
                putString(Constants.PIC_URL, item.imageUrl)
            }
            tempFragment
        }
        tempAdapter
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

        binding.btnMore.setLimitClickListener {
            binding.btnMore.setVisibleWithUnVisual(true)
            moreDialog?.show()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel._downLoadEvent.collect { isSuccess ->
                ToastUtils.showShort(
                    if (isSuccess)
                        getText(R.string.success_download)
                    else
                        getText(R.string.error_download)
                )
            }
        }
    }

    private fun updateTitleContent(selectedIndex: Int, isInit: Boolean = false) {
        val listSize = orderPicList.size
        if (isInit) {
            val targetPictureInfo = viewModel.picData.picList[selectedIndex]
            val targetSelectedIndex = orderPicList.indexOf(targetPictureInfo)
            // 每次轮播都将更新选中的index
            viewModel.updateSelectedIndex(targetSelectedIndex)
            binding.vpPic.setCurrentItem(targetSelectedIndex, false)
            binding.tvTitle.text =
                "${LocalCache.currentAlbum.title}(${targetSelectedIndex + 1}/${listSize})"
        } else {
            viewModel.updateSelectedIndex(selectedIndex)
            binding.tvTitle.text =
                "${LocalCache.currentAlbum.title}(${selectedIndex + 1}/${listSize})"
        }
    }

    private fun updateTitleStatus() {
        binding.mToolbar.setVisible(!viewModel.fullScreen)
        binding.containerBottom.setVisible(!viewModel.fullScreen)
        binding.mRootView.setBackgroundResource(
            if (viewModel.fullScreen)
                R.drawable.bg_4_black
            else
                R.drawable.bg_white
        )

        viewModel.fullScreen = !viewModel.fullScreen
    }
}
