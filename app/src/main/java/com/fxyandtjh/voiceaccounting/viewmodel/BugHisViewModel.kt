package com.fxyandtjh.voiceaccounting.viewmodel

import com.fxyandtjh.voiceaccounting.base.BaseViewModel
import com.fxyandtjh.voiceaccounting.net.response.BugInfo
import com.fxyandtjh.voiceaccounting.repository.impl.MainRepository
import com.fxyandtjh.voiceaccounting.repository.impl.StartupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class BugHisViewModel  @Inject constructor(
    private val startupRepository: StartupRepository
) : BaseViewModel() {

    private val pageData: MutableStateFlow<List<BugInfo>> = MutableStateFlow(emptyList())
    val _pageData: StateFlow<List<BugInfo>> = pageData

    init {
        getBugHistory()
    }

    fun getBugHistory() {
        launchUIWithDialog {
            pageData.value = startupRepository.getBugsHistory()
        }
    }
}