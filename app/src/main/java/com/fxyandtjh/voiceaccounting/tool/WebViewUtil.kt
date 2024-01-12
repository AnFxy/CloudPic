package com.fxyandtjh.voiceaccounting.tool

import androidx.navigation.NavController
import com.fxyandtjh.voiceaccounting.entity.WebViewInfo

class WebViewUtil private constructor() {

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { WebViewUtil() }
    }

    fun goLinkPage(navController: NavController, webViewInfo: WebViewInfo) {

    }
}