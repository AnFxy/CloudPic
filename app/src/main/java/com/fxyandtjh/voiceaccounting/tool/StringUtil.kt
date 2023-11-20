package com.fxyandtjh.voiceaccounting.tool

import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Date

fun encryptionPhoneNumber(phoneNumber: String): String {
    return if (phoneNumber.length >= 6) {
        // 取前面三位
        val preStr: String = phoneNumber.substring(0, 3)
        val endStr: String = phoneNumber.substring(phoneNumber.length - 3)
        "${preStr}*****${endStr}"
    } else {
        phoneNumber;
    }
}

// 时间戳转日期
fun timeStampToDate(timeStamp: Long): String {
    return try {
        val date = Date(timeStamp)
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        sdf.format(date)
    } catch (e: Exception) {
        e.printStackTrace()
        "-- -- --"
    }
}