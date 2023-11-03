package com.fxyandtjh.voiceaccounting.viewmodel

import com.fxyandtjh.voiceaccounting.base.BaseViewModel
import com.fxyandtjh.voiceaccounting.entity.LoginInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : BaseViewModel() {

    private var selectedLogin = MutableStateFlow<Boolean>(true)
    val _selectedLogin: StateFlow<Boolean> = selectedLogin

    private var selectedPassword = MutableStateFlow<Boolean>(false)
    val _selectedPassword: StateFlow<Boolean> = selectedPassword

    private var eyesClose = MutableStateFlow<Boolean>(true)
    val _eyesClose: StateFlow<Boolean> = eyesClose

    private var pageData = MutableStateFlow<LoginInfo>(
        LoginInfo(
            phoneNumber = "",
            password = "",
            confirmPassword = ""
        )
    )
    val _pageData: StateFlow<LoginInfo> = pageData

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
}