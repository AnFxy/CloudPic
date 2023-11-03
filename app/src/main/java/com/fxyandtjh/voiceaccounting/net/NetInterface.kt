package com.fxyandtjh.voiceaccounting.net

import com.fxyandtjh.voiceaccounting.entity.VoiceInfo
import retrofit2.http.Body
import retrofit2.http.POST

interface NetInterface {

    @POST("/server_api")
    suspend fun getTextFromVoiceByCloud(@Body body: Map<String, @JvmSuppressWildcards Any>): VoiceInfo
}