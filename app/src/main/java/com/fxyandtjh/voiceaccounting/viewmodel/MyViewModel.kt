package com.fxyandtjh.voiceaccounting.viewmodel

import com.fxyandtjh.voiceaccounting.BuildConfig
import com.fxyandtjh.voiceaccounting.base.BaseViewModel
import com.fxyandtjh.voiceaccounting.local.LocalCache
import com.fxyandtjh.voiceaccounting.net.response.UserInfo
import com.fxyandtjh.voiceaccounting.net.response.VersionInfo
import com.fxyandtjh.voiceaccounting.repository.impl.LoginRepository
import com.fxyandtjh.voiceaccounting.repository.impl.MainRepository
import com.fxyandtjh.voiceaccounting.repository.impl.StartupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MyViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val mainRepository: MainRepository,
    private val startupRepository: StartupRepository
) : BaseViewModel() {
    val version = BuildConfig.VERSION_NAME
    val cacheValue = "0KB"

    private val goLogin: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val _goLogin: SharedFlow<Boolean> = goLogin

    private val userInfoData: MutableStateFlow<UserInfo> = MutableStateFlow(
        UserInfo(
            name = "",
            headUrl = "",
            des = "",
            phoneNumber = LocalCache.phoneNumber,
            gender = 1,
            registerTime = 0,
            isBlack = 0
        )
    )
    val _userInfoData: StateFlow<UserInfo> = userInfoData

    private val showUpdate: MutableSharedFlow<VersionInfo> = MutableSharedFlow()
    val _showUpdate: SharedFlow<VersionInfo> = showUpdate

    init {
        obtainPersonalInformation()
    }

    fun doLogout() {
        launchUIWithDialog {
            loginRepository.doLogout(phoneNumber = LocalCache.phoneNumber, token = LocalCache.token)
            // 登出成功后，清除本地缓存，跳转到登录页面
            LocalCache.clearALLCache()
            goLogin.emit(true)
        }
    }

    fun obtainPersonalInformation() {
        launchUI {
            userInfoData.value = mainRepository.obtainUserInformation()
            // 将用户信息保存到本地
            LocalCache.userInfo = _userInfoData.value
        }
    }

    fun checkUpdate() {
        launchUIWithDialog {
            val versionInfo = startupRepository.checkUpdate()
            showUpdate.emit(versionInfo)
        }
    }
}