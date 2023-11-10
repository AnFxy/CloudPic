package com.fxyandtjh.voiceaccounting.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.fxyandtjh.voiceaccounting.base.BaseFragment
import com.fxyandtjh.voiceaccounting.databinding.FragSettingBinding
import com.fxyandtjh.voiceaccounting.viewmodel.SettingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : BaseFragment<SettingViewModel, FragSettingBinding>() {
    private val viewModel: SettingViewModel by viewModels()

    override fun getViewMode(): SettingViewModel = viewModel

    override fun getViewBinding(inflater: LayoutInflater, parent: ViewGroup?): FragSettingBinding =
        FragSettingBinding.inflate(inflater, parent, false)
}