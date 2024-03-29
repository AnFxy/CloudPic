package com.fxyandtjh.voiceaccounting.local

import com.fxyandtjh.voiceaccounting.base.Constants
import com.fxyandtjh.voiceaccounting.net.response.AlbumInfo
import com.fxyandtjh.voiceaccounting.net.response.CommonConfig
import com.fxyandtjh.voiceaccounting.net.response.UserInfo
import com.google.gson.reflect.TypeToken

class LocalCache {
    companion object {
        // 是否已经登录了 仅限于账号密码登录状态
        var isLogged: Boolean by SPSet<Boolean>(SPKeys.IS_LOGGED, false)

        // 用户登录类型 QQ登录 还是账号登录 或者微信登录
        var loginType: String by SPSet<String>(SPKeys.LOGIN_TYPE, Constants.ACCOUNT_PASSWORD_LOGIN)

        // token 仅限于账号密码登录状态
        var token: String by SPSet<String>(SPKeys.TOKEN, "")

        // QQ open id
        var qqOpenId: String by SPSet<String>(SPKeys.QQ_OPEN_ID, "")

        // QQ Access Token
        var qqToken: String by SPSet<String>(SPKeys.QQ_TOKEN, "")

        // QQ Token expire time
        var qqTokenExpireTime: Long by SPSet<Long>(SPKeys.QQ_TOKEN_EXPIRE_TIME, 0L)

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
            loginType = Constants.ACCOUNT_PASSWORD_LOGIN
            token = ""
            qqOpenId = ""
            qqToken = ""
            qqTokenExpireTime = 0L
            phoneNumber = ""
            userInfo = UserInfo(
                uid = 0,
                name = "",
                headUrl = "",
                des = "",
                phoneNumber = "",
                gender = 1,
                registerTime = 0,
                isBlack = 0
            )
            currentAlbum = AlbumInfo(
                faceUrl = "",
                title = "",
                labelId = 0,
                total = 0,
                createTime = 0,
                id = ""
            )
        }
    }
}