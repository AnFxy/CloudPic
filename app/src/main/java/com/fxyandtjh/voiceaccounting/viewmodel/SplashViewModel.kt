package com.fxyandtjh.voiceaccounting.viewmodel

import androidx.lifecycle.viewModelScope
import com.fxyandtjh.voiceaccounting.base.BaseViewModel
import com.fxyandtjh.voiceaccounting.net.response.VersionInfo
import com.fxyandtjh.voiceaccounting.repository.impl.StartupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val repository: StartupRepository
): BaseViewModel() {
    private val goHomePage: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val _goHomePage: SharedFlow<Boolean> = goHomePage

    private val showUpdate: MutableSharedFlow<VersionInfo> = MutableSharedFlow()
    val _showUpdate: SharedFlow<VersionInfo> = showUpdate

    init {
        checkUpdate()
    }

    private fun checkUpdate() {
        launchUI({
            val versionInfo = repository.checkUpdate()
            showUpdate.emit(versionInfo)
        }, {
            // 检测更新发生错误，那么不阻止用户流程
            goHomePage.emit(true)
        })
    }

    fun sendGoHomeEvent() {
        viewModelScope.launch {
            goHomePage.emit(true)
        }
    }
}
