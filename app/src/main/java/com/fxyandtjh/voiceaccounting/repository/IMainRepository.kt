package com.fxyandtjh.voiceaccounting.repository

import com.fxyandtjh.voiceaccounting.entity.NewAlbumInfo
import com.fxyandtjh.voiceaccounting.net.response.AccountSecurityMessage
import com.fxyandtjh.voiceaccounting.net.response.AlbumDetail
import com.fxyandtjh.voiceaccounting.net.response.AlbumInfo
import com.fxyandtjh.voiceaccounting.net.response.NotesInfo
import com.fxyandtjh.voiceaccounting.net.response.PictureInfo
import com.fxyandtjh.voiceaccounting.net.response.UserInfo

interface IMainRepository {
    // 获取用户的相册
    suspend fun getAlbumListFromRemote(): List<AlbumInfo>

    // 新建相册 标题、标签、封面
    suspend fun createAlbum(newAlbumInfo: NewAlbumInfo)

    // 获取相册的全部信息
    suspend fun getAlbumDetailFromRemote(albumId: String): AlbumDetail

    // 上传图片文件 图片要转为Base64 上传以及说明图片的类型
    suspend fun uploadFile(base64Str: String, type: String): PictureInfo

    // 更新相册（上传照片、删除照片） 图片链接、相册ID、更新类型
    suspend fun updateAlbumPic(imageUrlList: List<String>, albumId: String, updateType: String)

    // 更新相册信息、如标题、标签、封面
    suspend fun updateAlbumDetail(albumId: String, title: String, labelId: Int, faceUrl: String)

    // 删除整个相册
    suspend fun deleteAlbum(albumId: String, type: Int)

    // 获取用户信息
    suspend fun obtainUserInformation(): UserInfo

    // 更新用户信息
    suspend fun updateUserInformation(userInfo: UserInfo)

    // 获取云笔记数据（因需求变更，已经停止使用）
    suspend fun getNotesListFromRemote(): List<NotesInfo>

    // 获取用户账户安全信息
    suspend fun getUserAccountSecurityMessage(): AccountSecurityMessage

    // 添加和更新用户安全信息
    suspend fun updateUserAccountSecurity(questionId: Int, answer: String, type: Int)
}
