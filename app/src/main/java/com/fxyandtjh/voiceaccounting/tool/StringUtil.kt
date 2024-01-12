package com.fxyandtjh.voiceaccounting.tool

import android.text.TextUtils
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
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
fun timeStampToDate(timeStamp: Long, detail: Boolean = false): String {
    return try {
        val date = Date(timeStamp)
        val sdf = SimpleDateFormat(if (detail) "yyyy-MM-dd hh:mm:ss" else "yyyy-MM-dd")
        sdf.format(date)
    } catch (e: Exception) {
        e.printStackTrace()
        "-- -- --"
    }
}

// 时间转相册日期
fun timeStampToAlbumDate(timeStamp: Long): String {
    return try {
        val currentTime = LocalDate.now()
        val targetTime =
            Instant.ofEpochMilli(timeStamp).atZone(ZoneId.systemDefault()).toLocalDate()
        var targetDateText = ""
        // 判断 今天、昨天  今年
        if (currentTime.year == targetTime.year && currentTime.dayOfYear == targetTime.dayOfYear) {
            targetDateText = "今天"
        } else if (currentTime.year == targetTime.year && currentTime.dayOfYear == targetTime.dayOfYear + 1) {
            targetDateText = "昨天"
        } else {
            val date = Date(timeStamp)
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            targetDateText = sdf.format(date)
        }
        targetDateText
    } catch (e: Exception) {
        e.printStackTrace()
        "-- -- --"
    }
}

// 校验邮箱是否合法
fun String.isEmailValid(): Boolean {
    return !TextUtils.isEmpty(this) &&
            android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}
