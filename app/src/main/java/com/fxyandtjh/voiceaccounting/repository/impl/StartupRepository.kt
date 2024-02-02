package com.fxyandtjh.voiceaccounting.repository.impl

import com.fxyandtjh.voiceaccounting.net.NetInterface
import com.fxyandtjh.voiceaccounting.net.response.BugInfo
import com.fxyandtjh.voiceaccounting.net.response.CommonConfig
import com.fxyandtjh.voiceaccounting.net.response.VersionInfo
import com.fxyandtjh.voiceaccounting.repository.IStartupRepository
import com.fxyandtjh.voiceaccounting.tool.SecurityUtil
import java.lang.Exception
import javax.inject.Inject

class StartupRepository @Inject constructor(
    private val service: NetInterface
) : IStartupRepository {

    override suspend fun checkUpdate(): VersionInfo {
        checkProxy()
        return service.checkVersionUpdate().checkData();
    }

    override suspend fun uploadBugs(bugInfo: BugInfo) {
        checkProxy()
        val map = HashMap<String, Any>()
        map["type"] = bugInfo.type
        map["email"] = bugInfo.email
        map["content"] = bugInfo.content
        map["images"] = bugInfo.images
        service.uploadBugs(map).checkError()
    }

    override suspend fun getBugsHistory(): List<BugInfo> {
        checkProxy()
        return service.getBugsHistory().checkData()
    }

    override suspend fun getCommonConfig(): CommonConfig {
        checkProxy()
        return service.getInitCommonConfig()
    }

    private suspend fun checkProxy() {
        // 防止被抓包
        if (SecurityUtil.isWifiProxy()) {
            throw Exception("你开了WIFI代理，客户端不允许抓包，请关闭!")
        }
    }
}
