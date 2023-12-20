package com.fxyandtjh.voiceaccounting.viewmodel

import com.fxyandtjh.voiceaccounting.base.BaseViewModel
import com.fxyandtjh.voiceaccounting.entity.PicFile
import com.fxyandtjh.voiceaccounting.local.LocalCache
import com.fxyandtjh.voiceaccounting.net.response.AlbumInfo
import com.fxyandtjh.voiceaccounting.net.response.PictureInfo
import com.fxyandtjh.voiceaccounting.repository.impl.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : BaseViewModel() {
    private val albumInfo: MutableStateFlow<AlbumInfo> = MutableStateFlow(LocalCache.currentAlbum)
    val _albumInfo: StateFlow<AlbumInfo> = albumInfo

    private val picList: MutableStateFlow<List<PictureInfo>> = MutableStateFlow(emptyList())
    val _picList: StateFlow<List<PictureInfo>> = picList

    private val uploadSuccess: MutableSharedFlow<Pair<Int, Int>> = MutableSharedFlow()
    val _uploadSuccess: SharedFlow<Pair<Int, Int>> = uploadSuccess

    init {
        getAlbumFromRemote()
    }

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
        picList.value = albumDetail.picList
    }
}
