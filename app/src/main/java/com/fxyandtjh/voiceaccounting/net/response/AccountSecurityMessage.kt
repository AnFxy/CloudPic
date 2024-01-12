package com.fxyandtjh.voiceaccounting.net.response

data class AccountSecurityMessage(
    val uid: Long, // 用户ID
    val phoneNumber: String, // 用户手机
    val name: String, // 用户姓名
    val registerTime: Long, // 用户注册时间
    val securityQuestionId: Int, // 密保问题ID
)