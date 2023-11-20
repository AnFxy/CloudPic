package com.fxyandtjh.voiceaccounting.local

import com.fxyandtjh.voiceaccounting.net.response.UserInfo
import com.google.gson.reflect.TypeToken

class LocalCache {
    companion object {
        // 是否已经登录了
        var isLogged: Boolean by SPSet<Boolean>(SPKeys.IS_LOGGED, false)

        // token
        var token: String by SPSet<String>(SPKeys.TOKEN, "")

        // 手机号
        var phoneNumber: String by SPSet<String>(SPKeys.PHONE_NUMBER, "")

        // 用户信息
        var userInfo: UserInfo by SPSet<UserInfo>(
            SPKeys.USER_INFO,
            UserInfo(
                name = "",
                headUrl = "",
                des = "",
                phoneNumber = "",
                gender = 1,
                registerTime = 0,
                isBlack = 0
            ),
            object : TypeToken<UserInfo>() {}.type
        )

        fun clearALLCache() {
            isLogged = false
            token = ""
            phoneNumber = ""
        }
    }
}