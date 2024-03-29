package com.fxyandtjh.voiceaccounting.entity

data class QQLoginInfo(
    val access_token: String,
    val authority_cost: Int,
    val expires_in: Int,
    val expires_time: Long,
    val login_cost: Int,
    val msg: String,
    val openid: String,
    val pay_token: String,
    val pf: String,
    val pfkey: String,
    val query_authority_cost: Int,
    val ret: Int
)