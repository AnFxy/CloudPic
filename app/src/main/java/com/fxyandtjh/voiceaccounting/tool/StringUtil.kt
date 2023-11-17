package com.fxyandtjh.voiceaccounting.tool

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