package com.fxyandtjh.voiceaccounting.tool

import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.Utils
import com.fxyandtjh.voiceaccounting.R

class SimplePermissionCallBack(val onGranted: () -> Unit) : PermissionUtils.SimpleCallback {
    override fun onGranted() {
        onGranted.invoke()
    }

    override fun onDenied() {
        ToastUtils.showShort(Utils.getApp().getText(R.string.deny_permission))
    }
}
