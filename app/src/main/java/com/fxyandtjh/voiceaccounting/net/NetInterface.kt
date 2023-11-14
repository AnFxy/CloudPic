package com.fxyandtjh.voiceaccounting.net

import com.fxyandtjh.voiceaccounting.net.response.AlbumDetail
import com.fxyandtjh.voiceaccounting.net.response.AlbumInfo
import com.fxyandtjh.voiceaccounting.net.response.NotesInfo
import com.fxyandtjh.voiceaccounting.entity.VoiceInfo
import com.fxyandtjh.voiceaccounting.net.response.PictureInfo
import com.fxyandtjh.voiceaccounting.net.response.TokenInfo
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface NetInterface {

    @POST("/server_api")
    suspend fun getTextFromVoiceByCloud(@Body body: Map<String, @JvmSuppressWildcards Any>): VoiceInfo

    @POST("/register")
    suspend fun doRegister(@Body body: Map<String, String>): BaseResponse<Unit>

    @POST("/login")
    suspend fun doLogin(@Body body: Map<String, String>): BaseResponse<TokenInfo>

    @POST("/logout")
    suspend fun doLogout(@Body body: Map<String, String>): BaseResponse<Unit>

    @POST("/create_album")
    suspend fun createAlbum(@Body body: Map<String, @JvmSuppressWildcards Any>): BaseResponse<Unit>

    @GET("/get_albums")
    suspend fun getAlbums(): BaseResponse<List<AlbumInfo>>

    @POST("/get_album_detail")
    suspend fun getAlbumDetail(@Body body: Map<String, String>): BaseResponse<AlbumDetail>

    @POST("/upload/image")
    suspend fun uploadImage(@Body body: Map<String, String>): BaseResponse<PictureInfo>

    @POST("/update_album")
    suspend fun updateAlbum(@Body body: Map<String, @JvmSuppressWildcards Any>): BaseResponse<Unit>

    @POST("/delete_album")
    suspend fun deleteAlbum(@Body body: Map<String, String>): BaseResponse<Unit>

    @POST("/update_album_detail")
    suspend fun updateAlbumDetail(@Body body: Map<String, @JvmSuppressWildcards Any>): BaseResponse<Unit>

    @GET("/notes")
    suspend fun getNotes(): BaseResponse<List<NotesInfo>>
}
