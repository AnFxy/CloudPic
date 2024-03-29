package com.fxyandtjh.voiceaccounting.repository.impl

import com.fxyandtjh.voiceaccounting.entity.LoginInfo
import com.fxyandtjh.voiceaccounting.entity.QQLoginInfo
import com.fxyandtjh.voiceaccounting.net.NetInterface
import com.fxyandtjh.voiceaccounting.net.response.TokenInfo
import com.fxyandtjh.voiceaccounting.repository.ILoginRepository
import com.fxyandtjh.voiceaccounting.tool.SecurityUtil
import java.lang.Exception
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val service: NetInterface
) : ILoginRepository {

    override suspend fun doLogin(loginInfo: LoginInfo): TokenInfo {
        super.doLogin(loginInfo)
        checkProxy()
        val map = HashMap<String, String>()
        map["phoneNumber"] = loginInfo.phoneNumber
        map["password"] = loginInfo.password
        return service.doLogin(map).checkData()
    }

    override suspend fun doRegister(loginInfo: LoginInfo) {
        checkProxy()
        val map = HashMap<String, String>()
        map["name"] = "云用户0001"
        map["phoneNumber"] = loginInfo.phoneNumber
        map["password"] = loginInfo.password
        service.doRegister(map).checkError()
    }

    override suspend fun doLogout(phoneNumber: String, token: String) {
        checkProxy()
        val map = HashMap<String, String>()
        map["phoneNumber"] = phoneNumber
        map["token"] = token
        service.doLogout(map).checkError()
    }

    override suspend fun uploadQQLoginInfo(qqLoginInfo: QQLoginInfo) {
        checkProxy()

    }

    override suspend fun removeQQLoginInfo(qqOpenId: String, qqToken: String) {
        TODO("Not yet implemented")
    }

    private suspend fun checkProxy() {
        // 防止被抓包
//        if (SecurityUtil.isWifiProxy()) {
//            throw Exception("你开了WIFI代理，客户端不允许抓包，请关闭!")
//        }
    }
}
