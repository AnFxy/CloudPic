package com.fxyandtjh.voiceaccounting.net.response

data class VersionInfo (
    val versionCode: Long,
    val versionName: String,
    val miniVersionCode: Long,
    val title: String,
    val content: String,
    val downloadLink: String,
    val createTime: Long
)
