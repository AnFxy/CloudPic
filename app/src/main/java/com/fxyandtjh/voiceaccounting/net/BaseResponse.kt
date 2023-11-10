package com.fxyandtjh.voiceaccounting.net

import com.blankj.utilcode.util.ActivityUtils
import com.fxyandtjh.voiceaccounting.local.LocalCache

data class BaseResponse<T>(
    val data: T? = null,
    val code: Int? = null,
    val message: String? = null,
) {
    fun checkData(): T {
        checkError()
        return data ?: throw Exception("数据异常!")
    }

    fun checkError() {
        if (code == 101) {
            // 清除缓存，退出app
            LocalCache.clearALLCache()
            ActivityUtils.finishAllActivities()
            return
        } else {
            if (code != 200) {
                throw Exception("$message")
            }
        }
    }
}