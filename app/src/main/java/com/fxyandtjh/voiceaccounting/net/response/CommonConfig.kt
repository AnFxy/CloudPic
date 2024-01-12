package com.fxyandtjh.voiceaccounting.net.response

import com.fxyandtjh.voiceaccounting.entity.SecurityQuestion
import java.io.Serializable

data class CommonConfig(
    val privacyUrl: String,
    val userAgreementUrl: String,
    val securityQuestions: List<SecurityQuestion>
) : Serializable
