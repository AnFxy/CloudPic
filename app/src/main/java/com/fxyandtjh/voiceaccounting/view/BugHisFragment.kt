package com.fxyandtjh.voiceaccounting.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.fxyandtjh.voiceaccounting.adapter.BugAdapter
import com.fxyandtjh.voiceaccounting.base.BaseFragment
import com.fxyandtjh.voiceaccounting.databinding.FragBugHisBinding
import com.fxyandtjh.voiceaccounting.viewmodel.BugHisViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BugHisFragment : BaseFragment<BugHisViewModel, FragBugHisBinding>() {
    private val viewModel: BugHisViewModel by viewModels()

    private val mAdapter: BugAdapter by lazy {
        BugAdapter()
    }

    override fun getViewMode(): BugHisViewModel = viewModel

    override fun getViewBinding(inflater: LayoutInflater, parent: ViewGroup?): FragBugHisBinding =
        FragBugHisBinding.inflate(inflater, parent, false)

    override fun setLayout() {
        binding.rvBug.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }
    }

    override fun setObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel._pageData.collect {
                mAdapter.setNewInstance(it.toMutableList())
            }
        }
    }
}