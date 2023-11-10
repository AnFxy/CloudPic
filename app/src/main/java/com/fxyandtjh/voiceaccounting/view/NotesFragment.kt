package com.fxyandtjh.voiceaccounting.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.fxyandtjh.voiceaccounting.base.BaseFragment
import com.fxyandtjh.voiceaccounting.databinding.FragNotesBinding
import com.fxyandtjh.voiceaccounting.viewmodel.NotesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotesFragment : BaseFragment<NotesViewModel, FragNotesBinding>() {

    private val viewModel: NotesViewModel by viewModels()

    override fun getViewMode(): NotesViewModel = viewModel

    override fun getViewBinding(inflater: LayoutInflater, parent: ViewGroup?): FragNotesBinding =
        FragNotesBinding.inflate(inflater, parent, false)
}