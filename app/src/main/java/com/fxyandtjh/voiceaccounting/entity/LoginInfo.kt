package com.fxyandtjh.voiceaccounting.entity

data class LoginInfo(
    val phoneNumber: String,
    val password: String,
    val confirmPassword: String
)