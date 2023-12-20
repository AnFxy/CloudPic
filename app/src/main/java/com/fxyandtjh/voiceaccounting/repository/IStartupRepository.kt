package com.fxyandtjh.voiceaccounting.repository

import com.fxyandtjh.voiceaccounting.net.response.BugInfo
import com.fxyandtjh.voiceaccounting.net.response.VersionInfo

interface IStartupRepository {
    suspend fun checkUpdate(): VersionInfo

    suspend fun uploadBugs(bugInfo: BugInfo)

    suspend fun getBugsHistory(): List<BugInfo>
}
