package com.fxyandtjh.voiceaccounting.net

import com.fxyandtjh.voiceaccounting.entity.VoiceInfo
import com.fxyandtjh.voiceaccounting.net.response.AccountSecurityMessage
import com.fxyandtjh.voiceaccounting.net.response.AlbumDetail
import com.fxyandtjh.voiceaccounting.net.response.AlbumInfo
import com.fxyandtjh.voiceaccounting.net.response.BugInfo
import com.fxyandtjh.voiceaccounting.net.response.CommonConfig
import com.fxyandtjh.voiceaccounting.net.response.NotesInfo
import com.fxyandtjh.voiceaccounting.net.response.PictureInfo
import com.fxyandtjh.voiceaccounting.net.response.TokenInfo
import com.fxyandtjh.voiceaccounting.net.response.UserInfo
import com.fxyandtjh.voiceaccounting.net.response.VersionInfo
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface NetInterface {

    // 语音转文字
    @POST("/server_api")
    suspend fun getTextFromVoiceByCloud(@Body body: Map<String, @JvmSuppressWildcards Any>): VoiceInfo

    // 注册
    @POST("/register")
    suspend fun doRegister(@Body body: Map<String, String>): BaseResponse<Unit>

    // 登录
    @POST("/login")
    suspend fun doLogin(@Body body: Map<String, String>): BaseResponse<TokenInfo>

    // 登出
    @POST("/logout")
    suspend fun doLogout(@Body body: Map<String, String>): BaseResponse<Unit>

    // 上传QQ登录状态
    @POST("/update_qq_login_status")
    suspend fun updateQQLoginStatus(@Body body: Map<String, @JvmSuppressWildcards Any>): BaseResponse<Unit>

    // 移除QQ登录状态
    @POST("/remove_qq_login_status")
    suspend fun removeQQLoginStatus(@Body body: Map<String, String>): BaseResponse<Unit>

    // 创建相册
    @POST("/create_album")
    suspend fun createAlbum(@Body body: Map<String, @JvmSuppressWildcards Any>): BaseResponse<Unit>

    // 获取所有相册
    @GET("/get_albums")
    suspend fun getAlbums(): BaseResponse<List<AlbumInfo>>

    // 获取相册所有照片
    @POST("/get_album_detail")
    suspend fun getAlbumDetail(@Body body: Map<String, String>): BaseResponse<AlbumDetail>

    // 图片上传
    @POST("/upload/image")
    suspend fun uploadImage(@Body body: Map<String, String>): BaseResponse<PictureInfo>

    //  上传相册照片
    @POST("/update_album")
    suspend fun updateAlbum(@Body body: Map<String, @JvmSuppressWildcards Any>): BaseResponse<Unit>

    // 删除相册
    @POST("/delete_album")
    suspend fun deleteAlbum(@Body body: Map<String, String>): BaseResponse<Unit>

    // 更新相册详情页面
    @POST("/update_album_detail")
    suspend fun updateAlbumDetail(@Body body: Map<String, @JvmSuppressWildcards Any>): BaseResponse<Unit>

    // 获取云笔记（已经弃用）
    @GET("/notes")
    suspend fun getNotes(): BaseResponse<List<NotesInfo>>

    // 获取用户个人资料
    @GET("/userinfo")
    suspend fun getUserInformation(): BaseResponse<UserInfo>

    // 修改用户个人资料
    @POST("/update_userinfo")
    suspend fun updateUserInformation(@Body body: Map<String, @JvmSuppressWildcards Any>): BaseResponse<Unit>

    // 检测更新
    @POST("/check_update")
    suspend fun checkVersionUpdate(): BaseResponse<VersionInfo>

    // BUG反馈
    @POST("/commit_bug")
    suspend fun uploadBugs(@Body body: Map<String, @JvmSuppressWildcards Any>): BaseResponse<Unit>

    // 获取用户BUG反馈记录
    @POST("/bug_history")
    suspend fun getBugsHistory(): BaseResponse<List<BugInfo>>

    // 获取用户安全信息
    @POST("/account_security")
    suspend fun getAccountSecurity(): BaseResponse<AccountSecurityMessage>

    // 更新用户安全信息
    @POST("/update_security")
    suspend fun updateSecurity(@Body body: Map<String, @JvmSuppressWildcards Any>): BaseResponse<Unit>

    // 获取项目配置json
    @GET("/common/common.json")
    suspend fun getInitCommonConfig(): CommonConfig
}
