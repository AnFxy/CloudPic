package com.fxyandtjh.voiceaccounting.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.ToastUtils
import com.fxyandtjh.voiceaccounting.BuildConfig
import com.fxyandtjh.voiceaccounting.net.NetInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val serviceNet: NetInterface
) : ViewModel() {

    private var voiceData: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    val _voiceData: StateFlow<List<String>> = voiceData

    private var loading: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val _loading: SharedFlow<Boolean> = loading

    fun obtainTextFormVoice(base64Str: String, fileLength: Int) {
        val maper: HashMap<String, Any> = HashMap()
        maper["format"] = "m4a" // 音频源格式
        maper["rate"] = 16000  // 音频采集比率
        maper["channel"] = 1  // 声道数
        maper["cuid"] = DeviceUtils.getAndroidID() // 唯一标识手机
        maper["token"] = BuildConfig.TOKEN // 开发者TOken
        // maper["dev_pid"] = 1537
        maper["speech"] = base64Str // 音频数据 Base64格式
        maper["len"] = fileLength  //  音频数据长度 未转为Base64时的字节长度

        viewModelScope.launch {
            loading.emit(true)
            try {
                val res = serviceNet.getTextFromVoiceByCloud(maper)
                if (res.errorNo == 0 && !res.result.isNullOrEmpty()) {
                    voiceData.value = res.result
                } else {
                    voiceData.value = emptyList()
                    ToastUtils.showShort("识别失败，可能音频数据错误")
                }
            } catch (e: Exception) {
                voiceData.value = emptyList()
                e.printStackTrace()
                ToastUtils.showShort("识别失败，可能网络故障")
            } finally {
                loading.emit(false)
            }
        }
    }
}