package com.fxyandtjh.voiceaccounting.net.response

data class Subscriber(
    val headUrl: String,
    val name: String,
    val uid: Long,
    val isOwner: Int
)
