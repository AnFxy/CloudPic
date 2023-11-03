package com.fxyandtjh.voiceaccounting.local

class LocalCache {
    companion object {
        // 是否已经登录了
        var isLogged: Boolean by SPSet<Boolean>(SPKeys.IS_LOGGED, false)
        // token
        var token: String by SPSet<String>(SPKeys.TOKEN, "")
        // 手机号
        var phoneNumber: String by SPSet<String>(SPKeys.PHONE_NUMBER, "")
    }
}