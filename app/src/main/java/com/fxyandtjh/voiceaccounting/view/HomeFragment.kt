package com.fxyandtjh.voiceaccounting.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.fxyandtjh.voiceaccounting.R
import com.fxyandtjh.voiceaccounting.adapter.AlbumAdapter
import com.fxyandtjh.voiceaccounting.base.BaseFragment
import com.fxyandtjh.voiceaccounting.base.Constants
import com.fxyandtjh.voiceaccounting.base.receiveCallBackDataFromLastFragment
import com.fxyandtjh.voiceaccounting.databinding.FragHomeBinding
import com.fxyandtjh.voiceaccounting.local.LocalCache
import com.fxyandtjh.voiceaccounting.net.response.AlbumInfo
import com.fxyandtjh.voiceaccounting.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<HomeViewModel, FragHomeBinding>() {
    private val viewModel: HomeViewModel by viewModels()
    private val mAdapter: AlbumAdapter by lazy {
        val tempAdapter = AlbumAdapter { albumInfo ->
            // 点击后要判断是进入相册详情页面还是 进入新建相册页面
            handleClickAlbum(albumInfo)
        }
        val defaultFootView =
            LayoutInflater.from(context).inflate(R.layout.default_foot_view, binding.rvPic, false)
        tempAdapter.addFooterView(defaultFootView)
        tempAdapter
    }

    override fun getViewMode(): HomeViewModel = viewModel

    override fun getViewBinding(inflater: LayoutInflater, parent: ViewGroup?): FragHomeBinding =
        FragHomeBinding.inflate(inflater, parent, false)

    override fun setLayout() {
        context?.let {
            binding.rvPic.apply {
                layoutManager = GridLayoutManager(context, 2)
                adapter = mAdapter
            }
        }

        receiveCallBackDataFromLastFragment<Boolean>(key = Constants.NEW_ALBUM) {
            viewModel.getAlbumListFromRemote()
        }
    }

    override fun setObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel._albumList.collect {
                mAdapter.setNewInstance(it.toMutableList())
                updateAlbumAndPicCount(it)
            }
        }
    }

    override fun onRefresh() {
        viewModel.getAlbumListFromRemote()
    }

    private fun updateAlbumAndPicCount(albums: List<AlbumInfo>) {
        val albumsCount = if (albums.isEmpty()) 0 else albums.size - 1
        val picSize = albums.sumOf { item -> item.total }
        binding.tvCount.text = "${albumsCount}相册 ${picSize}图片"
    }

    private fun handleClickAlbum(albumInfo: AlbumInfo) {
        if (albumInfo.id == "") {
            navController.navigate(HomeFragmentDirections.actionHomeFragmentToNewAlbumFragment())
        } else {
            LocalCache.currentAlbum = albumInfo
            navController.navigate(HomeFragmentDirections.actionHomeFragmentToAlbumFragment())
        }
    }
}
