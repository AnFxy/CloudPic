package com.fxyandtjh.voiceaccounting.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.fxyandtjh.voiceaccounting.adapter.AlbumAdapter
import com.fxyandtjh.voiceaccounting.base.BaseFragment
import com.fxyandtjh.voiceaccounting.databinding.FragHomeBinding
import com.fxyandtjh.voiceaccounting.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<HomeViewModel, FragHomeBinding>() {
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var mAdapter: AlbumAdapter

    override fun getViewMode(): HomeViewModel = viewModel

    override fun getViewBinding(inflater: LayoutInflater, parent: ViewGroup?): FragHomeBinding =
        FragHomeBinding.inflate(inflater, parent, false)

    override fun setLayout() {
        mAdapter = AlbumAdapter()
        context?.let {
            binding.rvPic.apply {
                layoutManager = LinearLayoutManager(it)
                adapter = mAdapter
            }
        }
    }

    override fun setObserver() {

    }
}