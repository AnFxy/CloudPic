package com.fxyandtjh.voiceaccounting.repository

import com.fxyandtjh.voiceaccounting.net.response.VersionInfo

interface IStartupRepository {
    suspend fun checkUpdate(): VersionInfo
}