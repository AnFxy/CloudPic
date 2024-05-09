package com.fxyandtjh.voiceaccounting.qqsdk

import com.blankj.utilcode.util.ToastUtils
import com.fxyandtjh.voiceaccounting.entity.QQLoginInfo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tencent.tauth.IUiListener
import com.tencent.tauth.UiError

/**
 * 回调接口说明：
 * QQ登录、QQ快捷支付登录、应用分享到QQ、应用邀请等接口、都需要在调用相关功能时传入此回调接口
 * 回调接口将拿到 相关功能是否处理成功
 */
class QQBaseUiListener(private val onLoginSuccess: (QQLoginInfo) -> Unit) : IUiListener {
    override fun onComplete(p0: Any?) {
        p0?.let {
            ToastUtils.showShort("QQ登录授权成功！")
            val qqLoginInfo: QQLoginInfo = Gson().fromJson(it.toString(), object : TypeToken<QQLoginInfo>() {}.type)
            onLoginSuccess.invoke(qqLoginInfo)
        } ?: ToastUtils.showShort("QQ登录授权异常！")
    }

    override fun onError(p0: UiError?) {
        ToastUtils.showShort("QQ登录授权失败！")
    }

    override fun onCancel() {
        ToastUtils.showShort("QQ登录授权失败！")
    }

    override fun onWarning(p0: Int) {
        ToastUtils.showShort("QQ登录授权异常！")
    }
}