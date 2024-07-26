package com.fxyandtjh.voiceaccounting.entity

data class ApkProgress(
    val bytes: Long = 0L,
    val totalBytes: Long = 0L,
    val path: String = "",
    val code: Int = 200
)