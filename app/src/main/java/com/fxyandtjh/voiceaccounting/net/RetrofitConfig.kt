package com.fxyandtjh.voiceaccounting.net

import com.fxyandtjh.voiceaccounting.BuildConfig
import com.fxyandtjh.voiceaccounting.base.Constants
import com.fxyandtjh.voiceaccounting.local.LocalCache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitConfig private constructor() {
    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            RetrofitConfig()
        }
    }

    fun provideRetrofit(url: String = BuildConfig.BASE_URL): Retrofit {
        val okhttpBuilder = OkHttpClient.Builder()
        okhttpBuilder.run {
            retryOnConnectionFailure(true)
            // 添加请求头
            addInterceptor(
                Interceptor { chain ->
                    val request = chain.request().newBuilder()
                    val token = when (LocalCache.loginType) {
                        Constants.ACCOUNT_PASSWORD_LOGIN -> {
                            LocalCache.token
                        }
                        Constants.QQ_LOGOIN -> {
                            LocalCache.qqToken
                        }
                        else -> {
                            ""
                        }
                    }
                    request.addHeader("token", token)

                    chain.proceed(request.build())
                }
            )
            connectTimeout(12L, TimeUnit.SECONDS)
            readTimeout(12L, TimeUnit.SECONDS)
            writeTimeout(12L, TimeUnit.SECONDS)
        }
        return Retrofit.Builder()
            .baseUrl(url)
            .client(okhttpBuilder.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
