package com.fxyandtjh.voiceaccounting.repository.impl

import com.blankj.utilcode.util.ToastUtils
import com.fxyandtjh.voiceaccounting.entity.NewAlbumInfo
import com.fxyandtjh.voiceaccounting.net.NetInterface
import com.fxyandtjh.voiceaccounting.net.response.AccountSecurityMessage
import com.fxyandtjh.voiceaccounting.net.response.AlbumDetail
import com.fxyandtjh.voiceaccounting.net.response.AlbumInfo
import com.fxyandtjh.voiceaccounting.net.response.NotesInfo
import com.fxyandtjh.voiceaccounting.net.response.PictureInfo
import com.fxyandtjh.voiceaccounting.net.response.UserInfo
import com.fxyandtjh.voiceaccounting.repository.IMainRepository
import com.fxyandtjh.voiceaccounting.tool.SecurityUtil
import java.lang.Exception
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val service: NetInterface
) : IMainRepository {
    override suspend fun getAlbumListFromRemote(): List<AlbumInfo> {
        checkProxy()
        return service.getAlbums().checkData()
    }

    override suspend fun createAlbum(newAlbumInfo: NewAlbumInfo) {
        checkProxy()
        val map = HashMap<String, Any>()
        map["title"] = newAlbumInfo.title
        map["labelId"] = newAlbumInfo.currentSelectTag.weight
        map["faceUrl"] = newAlbumInfo.faceUrl
        service.createAlbum(map).checkError()
    }

    override suspend fun getAlbumDetailFromRemote(albumId: String): AlbumDetail {
        checkProxy()
        val map = HashMap<String, String>()
        map["albumId"] = albumId
        return service.getAlbumDetail(map).checkData()
    }

    override suspend fun uploadFile(base64Str: String, type: String): PictureInfo {
        checkProxy()
        val map = HashMap<String, String>()
        map["base64"] = base64Str
        map["type"] = type
        return service.uploadImage(map).checkData()
    }

    override suspend fun updateAlbumPic(
        imageUrlList: List<String>,
        albumId: String,
        updateType: String
    ) {
        checkProxy()
        val map = HashMap<String, Any>()
        map["imageUrlList"] = imageUrlList // 列表 List<String> String是图片链接字符串
        map["albumId"] = albumId
        map["updateType"] = updateType // 更新相册类型 添加add 移除remove
        return service.updateAlbum(map).checkError()
    }

    override suspend fun updateAlbumDetail(
        albumId: String,
        title: String,
        labelId: Int,
        faceUrl: String
    ) {
        checkProxy()
        val map = HashMap<String, Any>()
        map["albumId"] = albumId
        map["title"] = title
        map["labelId"] = labelId
        map["faceUrl"] = faceUrl
        return service.updateAlbumDetail(map).checkError()
    }

    override suspend fun deleteAlbum(albumId: String, type: Int) {
        checkProxy()
        val map = HashMap<String, Any>()
        map["albumId"] = albumId
        map["type"] = type
        return service.deleteAlbum(map).checkError()
    }

    override suspend fun obtainUserInformation(): UserInfo {
        checkProxy()
        return service.getUserInformation().checkData()
    }

    override suspend fun updateUserInformation(userInfo: UserInfo) {
        checkProxy()
        val map = HashMap<String, Any>()
        map["name"] = userInfo.name
        map["headUrl"] = userInfo.headUrl
        map["gender"] = userInfo.gender
        map["des"] = userInfo.des
        service.updateUserInformation(map).checkError()
    }

    override suspend fun getNotesListFromRemote(): List<NotesInfo> {
        checkProxy()
        return service.getNotes().checkData()
    }

    override suspend fun getUserAccountSecurityMessage(): AccountSecurityMessage {
        checkProxy()
        return service.getAccountSecurity().checkData()
    }

    override suspend fun updateUserAccountSecurity(questionId: Int, answer: String, type: Int) {
        checkProxy()
        val map = HashMap<String, Any>()
        map["questionId"] = questionId
        map["answer"] = answer
        map["type"] = type
        service.updateSecurity(map).checkError()
    }

    private suspend fun checkProxy() {
        // 防止被抓包
//        if (SecurityUtil.isWifiProxy()) {
//            throw Exception("您开了WIFI代理，客户端不允许抓包，请关闭!")
//        }
    }
}
