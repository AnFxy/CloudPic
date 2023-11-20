package com.fxyandtjh.voiceaccounting.base

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

/**
 * 使用场景， 比如 A fragment跳到 B fragment希望, B fragment返回销毁时， 把数据带回 A fragment。
 *
 */
// 数据发送
fun <T : Any> Fragment.FragDestroyCallBack(key: String, data: T, isNeedBack: Boolean = true) {
    findNavController().previousBackStackEntry?.savedStateHandle?.set(key, data)
    if (isNeedBack) {
        findNavController().popBackStack()
    }
}

// 数据接收
fun <T : Any> Fragment.receiveCallBackDataFromLastFragment(key: String, callBack: (T) -> Unit) {
    findNavController().currentBackStackEntry?.savedStateHandle
        ?.apply {
            getLiveData<T>(key).observe(viewLifecycleOwner) {
                callBack(it)
            }
            remove<T>(key)
        }
}