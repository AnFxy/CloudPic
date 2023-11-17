package com.fxyandtjh.voiceaccounting.viewmodel

import com.fxyandtjh.voiceaccounting.base.BaseViewModel
import com.fxyandtjh.voiceaccounting.local.LocalCache
import com.fxyandtjh.voiceaccounting.net.response.UserInfo
import com.fxyandtjh.voiceaccounting.repository.impl.LoginRepository
import com.fxyandtjh.voiceaccounting.repository.impl.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MyViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val mainRepository: MainRepository
) : BaseViewModel() {

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

    init {
        obtainPersonalInformation()
    }

    fun doLogout () {
        launchUIWithDialog {
            loginRepository.doLogout(phoneNumber = LocalCache.phoneNumber, token = LocalCache.token)
            // 登出成功后，清除本地缓存，跳转到登录页面
            LocalCache.clearALLCache()
            goLogin.emit(true)
        }
    }

    fun obtainPersonalInformation () {
        launchUIWithDialog {
            userInfoData.value = mainRepository.obtainUserInformation()
        }
    }
}