package com.fxyandtjh.voiceaccounting.viewmodel

import androidx.lifecycle.viewModelScope
import com.fxyandtjh.voiceaccounting.base.BaseViewModel
import com.fxyandtjh.voiceaccounting.entity.ApkProgress
import com.fxyandtjh.voiceaccounting.local.LocalCache
import com.fxyandtjh.voiceaccounting.net.response.VersionInfo
import com.fxyandtjh.voiceaccounting.repository.impl.StartupRepository
import com.fxyandtjh.voiceaccounting.tool.DownLoadApk
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val repository: StartupRepository
) : BaseViewModel() {
    private val goHomePage: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val _goHomePage: SharedFlow<Boolean> = goHomePage

    private val showUpdate: MutableSharedFlow<VersionInfo> = MutableSharedFlow()
    val _showUpdate: SharedFlow<VersionInfo> = showUpdate

    private val apkProgress: MutableStateFlow<ApkProgress?> = MutableStateFlow(null)
    val _apkProgress: StateFlow<ApkProgress?> = apkProgress

    var lastDownLoadValue = 0L

    private val startCalculateSpeed: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val _startCalculateSpeed: SharedFlow<Boolean> = startCalculateSpeed

    init {
        checkUpdate()
    }

    private fun checkUpdate() {
        launchUI({
            val a = repository.getCommonConfig()
            LocalCache.commonConfig = a
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

    fun startUpdateApp(downloadLink: String) {
        viewModelScope.launch {
            startCalculateSpeed.emit(true)
        }
        DownLoadApk.instance.download(downloadLink) { apkProgress ->
            if (apkProgress.path != "") {
                this.apkProgress.value = _apkProgress.value?.copy(path = apkProgress.path)
            } else if (apkProgress.code != 200) {
                // 下载失败
            } else {
                this.apkProgress.value = apkProgress
            }
        }
    }
}
