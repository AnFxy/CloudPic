package com.fxyandtjh.voiceaccounting.repository

import com.fxyandtjh.voiceaccounting.entity.LoginInfo
import com.fxyandtjh.voiceaccounting.entity.QQLoginInfo
import com.fxyandtjh.voiceaccounting.net.response.TokenInfo

interface ILoginRepository {
    suspend fun doLogin(loginInfo: LoginInfo): TokenInfo

    suspend fun doRegister(loginInfo: LoginInfo)

    suspend fun doLogout(phoneNumber: String, token: String)

    suspend fun uploadQQLoginInfo(qqLoginInfo: QQLoginInfo)

    suspend fun removeQQLoginInfo(qqOpenId: String, qqToken: String)
}
