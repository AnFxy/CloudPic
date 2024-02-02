package com.fxyandtjh.voiceaccounting.local

import com.fxyandtjh.voiceaccounting.net.response.AlbumInfo
import com.fxyandtjh.voiceaccounting.net.response.CommonConfig
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

        // 隐私协议链接、用户协议链接、密保问题
        var commonConfig: CommonConfig by SPSet<CommonConfig>(
            SPKeys.COMMON_CONFIG,
            CommonConfig(
                privacyUrl = "",
                userAgreementUrl = "",
                beiAnUrl = "",
                securityQuestions = emptyList()
            ),
            object : TypeToken<CommonConfig>() {}.type
        )

        // 用户信息
        var userInfo: UserInfo by SPSet<UserInfo>(
            SPKeys.USER_INFO,
            UserInfo(
                uid = 0,
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

        // 当前相册信息
        var currentAlbum: AlbumInfo by SPSet<AlbumInfo>(
            SPKeys.CURRENT_ALBUM,
            AlbumInfo(
                faceUrl = "",
                title = "",
                labelId = 0,
                total = 0,
                createTime = 0,
                id = ""
            ),
            object : TypeToken<AlbumInfo>() {}.type
        )

        fun clearALLCache() {
            isLogged = false
            token = ""
            phoneNumber = ""
        }
    }
}