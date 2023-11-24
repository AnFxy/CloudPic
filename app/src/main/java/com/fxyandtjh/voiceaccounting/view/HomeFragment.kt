package com.fxyandtjh.voiceaccounting.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.fxyandtjh.voiceaccounting.adapter.AlbumAdapter
import com.fxyandtjh.voiceaccounting.base.BaseFragment
import com.fxyandtjh.voiceaccounting.databinding.FragHomeBinding
import com.fxyandtjh.voiceaccounting.net.response.AlbumInfo
import com.fxyandtjh.voiceaccounting.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<HomeViewModel, FragHomeBinding>() {
    private val viewModel: HomeViewModel by viewModels()
    private val mAdapter: AlbumAdapter by lazy {
        AlbumAdapter()
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
    }

    override fun setObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel._albumList.collect {
                mAdapter.setNewInstance(it.toMutableList())
                updateAlbumAndPicCount(it)
            }
        }
    }

    private fun updateAlbumAndPicCount(albums: List<AlbumInfo>) {
        val albumsCount = if (albums.isEmpty()) 0 else albums.size - 1
        val picSize = albums.sumOf { item -> item.total }
        binding.tvCount.text = "${albumsCount}相册 ${picSize}图片"
    }
}