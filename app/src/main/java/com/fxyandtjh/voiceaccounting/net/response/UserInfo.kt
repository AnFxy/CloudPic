package com.fxyandtjh.voiceaccounting.net.response

data class UserInfo(
    val name: String,
    val headUrl: String,
    val des: String,
    val phoneNumber: String,
    val gender: Int,
    val registerTime: Long,
    val isBlack: Int
)
