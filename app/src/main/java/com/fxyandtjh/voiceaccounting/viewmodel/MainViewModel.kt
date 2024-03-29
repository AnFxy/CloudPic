package com.fxyandtjh.voiceaccounting.viewmodel

import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.ToastUtils
import com.fxyandtjh.voiceaccounting.BuildConfig
import com.fxyandtjh.voiceaccounting.base.BaseViewModel
import com.fxyandtjh.voiceaccounting.entity.QQLoginInfo
import com.fxyandtjh.voiceaccounting.net.NetInterface
import com.fxyandtjh.voiceaccounting.repository.ILoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val loginRepository: ILoginRepository
) : BaseViewModel() {

    private var voiceData: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    val _voiceData: StateFlow<List<String>> = voiceData

    private var goHomePage: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val _goHomePage: SharedFlow<Boolean> = goHomePage

    fun uploadQQLoginInfo(qqLoginInfo: QQLoginInfo) {

    }


}