package com.fxyandtjh.voiceaccounting.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.fxyandtjh.voiceaccounting.base.BaseFragment
import com.fxyandtjh.voiceaccounting.databinding.FragMyBinding
import com.fxyandtjh.voiceaccounting.viewmodel.MyViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyFragment : BaseFragment<MyViewModel, FragMyBinding>() {
    private val viewModel: MyViewModel by viewModels()

    override fun getViewMode(): MyViewModel = viewModel

    override fun getViewBinding(inflater: LayoutInflater, parent: ViewGroup?): FragMyBinding =
        FragMyBinding.inflate(inflater, parent, false)
}