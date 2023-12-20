package com.fxyandtjh.voiceaccounting.repository.impl

import com.fxyandtjh.voiceaccounting.entity.LoginInfo
import com.fxyandtjh.voiceaccounting.net.NetInterface
import com.fxyandtjh.voiceaccounting.net.response.TokenInfo
import com.fxyandtjh.voiceaccounting.repository.ILoginRepository
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val service: NetInterface
) : ILoginRepository {

    override suspend fun doLogin(loginInfo: LoginInfo): TokenInfo {
        val map = HashMap<String, String>()
        map["phoneNumber"] = loginInfo.phoneNumber
        map["password"] = loginInfo.password
        return service.doLogin(map).checkData()
    }

    override suspend fun doRegister(loginInfo: LoginInfo) {
        val map = HashMap<String, String>()
        map["name"] = "云用户0001"
        map["phoneNumber"] = loginInfo.phoneNumber
        map["password"] = loginInfo.password
        service.doRegister(map).checkError()
    }

    override suspend fun doLogout(phoneNumber: String, token: String) {
        val map = HashMap<String, String>()
        map["phoneNumber"] = phoneNumber
        map["token"] = token
        service.doLogout(map).checkError()
    }
}
