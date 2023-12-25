package com.fxyandtjh.voiceaccounting.viewmodel

import com.blankj.utilcode.util.ToastUtils
import com.fxyandtjh.voiceaccounting.base.BaseViewModel
import com.fxyandtjh.voiceaccounting.entity.LoginInfo
import com.fxyandtjh.voiceaccounting.local.LocalCache
import com.fxyandtjh.voiceaccounting.repository.impl.LoginRepository
import com.fxyandtjh.voiceaccounting.repository.impl.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val mainRepository: MainRepository
) : BaseViewModel() {

    private val selectedLogin = MutableStateFlow<Boolean>(true)
    val _selectedLogin: StateFlow<Boolean> = selectedLogin

    private val selectedPassword = MutableStateFlow<Boolean>(false)
    val _selectedPassword: StateFlow<Boolean> = selectedPassword

    private val eyesClose = MutableStateFlow<Boolean>(true)
    val _eyesClose: StateFlow<Boolean> = eyesClose

    private var pageData = MutableStateFlow<LoginInfo>(
        LoginInfo(
            phoneNumber = "",
            password = "",
            confirmPassword = ""
        )
    )
    val _pageData: StateFlow<LoginInfo> = pageData

    // 事件
    private val goHome = MutableSharedFlow<Boolean>()
    val _goHome: SharedFlow<Boolean> = goHome

    fun changeSelectedState() {
        this.selectedLogin.value = !this.selectedLogin.value
    }

    fun updateSelectedPassword(value: Boolean) {
        this.selectedPassword.value = value
    }

    fun updatePhoneNumber(newValue: String) {
        pageData.value = pageData.value.copy(phoneNumber = newValue)
    }

    fun updatePw(newValue: String) {
        pageData.value = pageData.value.copy(password = newValue)
    }

    fun updateConfirmPw(newValue: String) {
        pageData.value = pageData.value.copy(confirmPassword = newValue)
    }

    fun updateEyes(isClosed: Boolean) {
        eyesClose.value = isClosed
    }

    fun doLogin() {
        launchUIWithDialog {
            delay(1500)
            val tokenInfo = loginRepository.doLogin(_pageData.value)
            // 登录成功后，将Token保存到本地
            LocalCache.isLogged = true
            LocalCache.token = tokenInfo.token
            LocalCache.phoneNumber = _pageData.value.phoneNumber
            // 请求获取用户个人信息
            LocalCache.userInfo = mainRepository.obtainUserInformation()
            // 通知前往home页面
            goHome.emit(true)
        }
    }

    fun doRegister() {
        launchUIWithDialog {
            delay(1500)
            loginRepository.doRegister(_pageData.value)
            ToastUtils.showShort("注册成功")
            updateConfirmPw("")
            selectedLogin.value = true
        }
    }
}