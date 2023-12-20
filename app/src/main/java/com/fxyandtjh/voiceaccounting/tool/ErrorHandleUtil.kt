package com.fxyandtjh.voiceaccounting.tool

import com.blankj.utilcode.util.ToastUtils
import kotlinx.coroutines.TimeoutCancellationException
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.ParseException
import java.util.concurrent.TimeoutException
import javax.net.ssl.SSLHandshakeException

class ErrorHandleUtil {
    companion object {
        // 处理异常类型
        fun handleError(e: Exception) {
            val error = e.let {
                when (it) {
                    is SocketTimeoutException,
                    is TimeoutException,
                    is TimeoutCancellationException,
                    is ConnectException,
                    is UnknownHostException,
                    is SSLHandshakeException -> {
                        "网络异常！"
                    }

                    is ParseException -> {       // ParseException异常表明请求成功，但是数据不正确
                        it.message ?: "数据解析错误!"   //msg为空，显示code
                    }

                    is HttpException -> {
                        "网络异常！"
                    }

                    is IOException -> {
                        "文件读写异常！"
                    }

                    else -> {
                        it.message ?: "未知错误!"
                    }
                }
            }
            if (error.isNotEmpty()) {
                ToastUtils.showLong(error)
            }
            e.printStackTrace()
        }
    }
}
