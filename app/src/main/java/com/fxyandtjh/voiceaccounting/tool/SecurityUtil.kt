package com.fxyandtjh.voiceaccounting.tool

import android.text.TextUtils

class SecurityUtil {
    companion object {
        fun isWifiProxy(): Boolean {
            val proxyAddress = System.getProperty("http.proxyHost")
            val portStr = System.getProperty("http.proxyPort")
            val proxyPort = Integer.parseInt((portStr ?: "-1"))
            return (!TextUtils.isEmpty(proxyAddress)) && (proxyPort != -1)
        }
    }
}
