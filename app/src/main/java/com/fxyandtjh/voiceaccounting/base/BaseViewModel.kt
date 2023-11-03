package com.fxyandtjh.voiceaccounting.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

abstract class BaseViewModel : ViewModel() {

    protected val loading : MutableSharedFlow<Boolean> = MutableSharedFlow()
    val _loading: SharedFlow<Boolean> = loading
}