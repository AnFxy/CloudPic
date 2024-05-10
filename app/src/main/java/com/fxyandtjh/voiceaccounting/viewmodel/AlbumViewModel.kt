package com.fxyandtjh.voiceaccounting.viewmodel

import android.util.Base64
import android.util.Log
import com.fxyandtjh.voiceaccounting.base.BaseViewModel
import com.fxyandtjh.voiceaccounting.entity.ExtraPictureInfo
import com.fxyandtjh.voiceaccounting.entity.PicEditType
import com.fxyandtjh.voiceaccounting.entity.PicFile
import com.fxyandtjh.voiceaccounting.local.LocalCache
import com.fxyandtjh.voiceaccounting.net.response.AlbumInfo
import com.fxyandtjh.voiceaccounting.net.response.PictureInfo
import com.fxyandtjh.voiceaccounting.net.response.Subscriber
import com.fxyandtjh.voiceaccounting.repository.impl.MainRepository
import com.fxyandtjh.voiceaccounting.tool.PicLoadUtil
import com.luck.picture.lib.entity.LocalMedia
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.io.FileInputStream
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : BaseViewModel() {
    private val albumInfo: MutableStateFlow<AlbumInfo> = MutableStateFlow(LocalCache.currentAlbum)
    val _albumInfo: StateFlow<AlbumInfo> = albumInfo

    private val picList: MutableStateFlow<List<ExtraPictureInfo>> = MutableStateFlow(emptyList())
    val _picList: StateFlow<List<ExtraPictureInfo>> = picList

    private val uploadSuccess: MutableSharedFlow<Pair<Int, Int>> = MutableSharedFlow()
    val _uploadSuccess: SharedFlow<Pair<Int, Int>> = uploadSuccess

    private val selectMode: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val _selectMode: StateFlow<Boolean> = selectMode

    private val deleteSuccess: MutableSharedFlow<Unit> = MutableSharedFlow()
    val _deleteSuccess: SharedFlow<Unit> = deleteSuccess

    private val subscribers: MutableStateFlow<List<Subscriber>> = MutableStateFlow(emptyList())
    val _subscribers: StateFlow<List<Subscriber>> = subscribers

    private val goBack: MutableSharedFlow<Unit> = MutableSharedFlow()
    val _goBack: SharedFlow<Unit> = goBack

    var isAllSelected: Boolean = false

    init {
        getAlbumFromRemote()
    }

    // 获取云相册
    fun getAlbumFromRemote() {
        launchUIWithDialog {
            if (_albumInfo.value.id != "") {
                refreshAlbum()
            }
        }
    }

    // 上传图片到云服务器
    fun uploadPictures(picFiles: List<PicFile>) {
        launchUIWithDialog {
            val picUrls = ArrayList<PictureInfo>()
            var uploadedCount = 0
            uploadSuccess.emit(Pair(uploadedCount, picFiles.size))
            picFiles.forEach { item ->
                picUrls.add(mainRepository.uploadFile(item.base64, item.type))
                uploadedCount++
                uploadSuccess.emit(Pair(uploadedCount, picFiles.size))
            }
            // 服务器上传成功后 将其插入相册
            mainRepository.updateAlbumPic(
                imageUrlList = picUrls.map { item -> item.imageUrl },
                albumId = _albumInfo.value.id,
                updateType = "add"
            )
            // 并刷新相册图片
            refreshAlbum()
        }
    }

    // 刷新云相册
    private suspend fun refreshAlbum() {
        val albumDetail = mainRepository.getAlbumDetailFromRemote(_albumInfo.value.id)
        albumInfo.value = _albumInfo.value.copy(
            faceUrl = albumDetail.faceUrl,
            title = albumDetail.title,
            labelId = albumDetail.labelId,
            total = albumDetail.total,
            createTime = albumDetail.createTime,
            id = albumDetail.albumId
        )
        picList.value = albumDetail.picList.map { item ->
            ExtraPictureInfo(pictureInfo = item)
        }
        subscribers.value = albumDetail.subscribers
    }

    // 编辑模式开启与关闭
    fun updateSelectMode(isSelect: Boolean) {
        this.selectMode.value = isSelect

        this.picList.value =
            this._picList.value.map { item -> item.copy(showSelect = isSelect, selected = false) }
    }

    // 选中图片
    fun updateCheckMode(targetItem: ExtraPictureInfo, isChecked: Boolean) {
        val targetIndex = this._picList.value.indexOf(targetItem)
        if (targetIndex != -1) {
            this.picList.value = this._picList.value.mapIndexed { index, extraPictureInfo ->
                if (index == targetIndex) {
                    extraPictureInfo.copy(selected = isChecked)
                } else {
                    extraPictureInfo
                }
            }
        }

    }

    // 点击全选
    fun updateCheckAllMode() {
        this.isAllSelected = !this.isAllSelected
        this.picList.value =
            this._picList.value.map { item -> item.copy(selected = this.isAllSelected) }
    }

    // 删除所选的照片
    fun deleteSelectedPics() {
        launchUIWithDialog {
            mainRepository.updateAlbumPic(
                imageUrlList = _picList.value
                    .filter { item -> item.selected }.map { it.pictureInfo.imageUrl },
                albumId = _albumInfo.value.id,
                updateType = PicEditType.REMOVE.value
            )
            deleteSuccess.emit(Unit)
        }
    }

    // 删除相册
    fun deleteAlbum () {
        launchUIWithDialog {
            val uid = LocalCache.userInfo.uid
            val targetSubscriber = subscribers.value.filter { it.uid == LocalCache.userInfo.uid }

            mainRepository.deleteAlbum(albumId = _albumInfo.value.id, type = targetSubscriber[0].isOwner)
            goBack.emit(Unit)
        }
    }
}
