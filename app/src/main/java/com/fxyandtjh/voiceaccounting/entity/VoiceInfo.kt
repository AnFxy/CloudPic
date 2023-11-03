package com.fxyandtjh.voiceaccounting.entity

import com.google.gson.annotations.SerializedName

class VoiceInfo(
    @SerializedName("err_no")
    val errorNo: Int,

    @SerializedName("err_msg")
    val errorMsg: String,

    val result: List<String>?
)