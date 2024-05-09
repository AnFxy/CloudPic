package com.fxyandtjh.voiceaccounting.viewmodel

import com.fxyandtjh.voiceaccounting.base.BaseViewModel
import com.fxyandtjh.voiceaccounting.base.Constants
import com.fxyandtjh.voiceaccounting.entity.QQLoginInfo
import com.fxyandtjh.voiceaccounting.local.LocalCache
import com.fxyandtjh.voiceaccounting.repository.ILoginRepository
import com.fxyandtjh.voiceaccounting.repository.IMainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val loginRepository: ILoginRepository,
    private val mainRepository: IMainRepository
) : BaseViewModel() {
    private var goHomePage: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val _goHomePage: SharedFlow<Boolean> = goHomePage

    fun uploadQQLoginInfo(qqLoginInfo: QQLoginInfo) {
        launchUIWithDialog {
            loginRepository.uploadQQLoginInfo(qqLoginInfo)
            // 登录成功后，将Token保存到本地
            LocalCache.isLogged = true
            LocalCache.qqToken = qqLoginInfo.access_token
            LocalCache.qqOpenId = qqLoginInfo.openid
            LocalCache.qqTokenExpireTime = qqLoginInfo.expires_time
            LocalCache.loginType = Constants.QQ_LOGOIN
            // 请求获取用户个人信息
            LocalCache.userInfo = mainRepository.obtainUserInformation()
            goHomePage.emit(true)
        }
    }
}