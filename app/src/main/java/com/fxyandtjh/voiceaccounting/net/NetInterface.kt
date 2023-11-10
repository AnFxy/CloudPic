package com.fxyandtjh.voiceaccounting.net

import com.fxyandtjh.voiceaccounting.entity.VoiceInfo
import com.fxyandtjh.voiceaccounting.net.response.TokenInfo
import retrofit2.http.Body
import retrofit2.http.POST

interface NetInterface {

    @POST("/server_api")
    suspend fun getTextFromVoiceByCloud(@Body body: Map<String, @JvmSuppressWildcards Any>): VoiceInfo

    @POST("/register")
    suspend fun doRegister(@Body body: Map<String, String>): BaseResponse<Any>

    @POST("/login")
    suspend fun doLogin(@Body body: Map<String, String>): BaseResponse<TokenInfo>

    @POST("/logout")
    suspend fun doLogout(@Body body: Map<String, String>): BaseResponse<Any>

}