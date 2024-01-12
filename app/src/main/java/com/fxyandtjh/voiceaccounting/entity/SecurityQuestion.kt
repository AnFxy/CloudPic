package com.fxyandtjh.voiceaccounting.entity

import java.io.Serializable

data class SecurityQuestion(
    val id: Int,
    val question: String
) : Serializable
