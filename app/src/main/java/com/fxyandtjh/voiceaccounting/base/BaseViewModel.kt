package com.fxyandtjh.voiceaccounting.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fxyandtjh.voiceaccounting.tool.ErrorHandleUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    protected val loading: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val _loading: SharedFlow<Boolean> = loading

    //运行在UI线程的协程
    fun launchUIWithDialog(
        block: suspend CoroutineScope.() -> Unit,
        onError: suspend CoroutineScope.() -> Unit
    ) = viewModelScope.launch {
        try {
            // loading事件只在BaseActivity中消费
            loading.emit(true)
            block()
            loading.emit(false)
        } catch (e: Exception) {
            ErrorHandleUtil.handleError(e)
            loading.emit(false)
            onError()
        }
    }

    fun launchUIWithDialog(block: suspend CoroutineScope.() -> Unit) = launchUIWithDialog(block) {}

    fun launchUI(
        block: suspend CoroutineScope.() -> Unit,
        onError: suspend CoroutineScope.() -> Unit
    ) = viewModelScope.launch {
        try {
            // loading事件只在BaseActivity中消费
            block()
        } catch (e: Exception) {
            ErrorHandleUtil.handleError(e)
            onError()
        }
    }

    fun launchUI(block: suspend CoroutineScope.() -> Unit) = launchUI(block) {}
}