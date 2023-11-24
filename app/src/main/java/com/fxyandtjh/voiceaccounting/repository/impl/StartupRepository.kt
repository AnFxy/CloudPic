package com.fxyandtjh.voiceaccounting.repository.impl

import com.fxyandtjh.voiceaccounting.net.NetInterface
import com.fxyandtjh.voiceaccounting.net.response.BugInfo
import com.fxyandtjh.voiceaccounting.net.response.VersionInfo
import com.fxyandtjh.voiceaccounting.repository.IStartupRepository
import javax.inject.Inject

class StartupRepository @Inject constructor(
    private val service: NetInterface
) : IStartupRepository {

    override suspend fun checkUpdate(): VersionInfo {
        return service.checkVersionUpdate().checkData();
    }

    override suspend fun uploadBugs(bugInfo: BugInfo) {
        val map = HashMap<String, Any>()
        map["type"] = bugInfo.type
        map["email"] = bugInfo.email
        map["content"] = bugInfo.content
        map["images"] = bugInfo.images
        service.uploadBugs(map).checkError()
    }

    override suspend fun getBugsHistory(): List<BugInfo> {
        return service.getBugsHistory().checkData()
    }
}
