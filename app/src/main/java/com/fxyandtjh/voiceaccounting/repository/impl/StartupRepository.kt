package com.fxyandtjh.voiceaccounting.repository.impl

import com.fxyandtjh.voiceaccounting.net.NetInterface
import com.fxyandtjh.voiceaccounting.net.response.VersionInfo
import com.fxyandtjh.voiceaccounting.repository.IStartupRepository
import javax.inject.Inject

class StartupRepository @Inject constructor(
    private val service: NetInterface
) : IStartupRepository {

    override suspend fun checkUpdate(): VersionInfo {
        return service.checkVersionUpdate().checkData();
    }
}
