package com.fxyandtjh.voiceaccounting.viewmodel

import android.graphics.Bitmap
import com.fxyandtjh.voiceaccounting.base.BaseViewModel
import com.fxyandtjh.voiceaccounting.entity.NewAlbumInfo
import com.fxyandtjh.voiceaccounting.entity.Type
import com.fxyandtjh.voiceaccounting.repository.impl.MainRepository
import com.fxyandtjh.voiceaccounting.tool.HandlePhoto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NewAlbumViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : BaseViewModel() {

    private val pageData: MutableStateFlow<NewAlbumInfo> = MutableStateFlow(
        NewAlbumInfo(
            title = "",
            faceUrl = "",
            currentSelectTag = Type.NORMAL
        )
    )
    val _pageData: StateFlow<NewAlbumInfo> = pageData

    private val createAlbumSuccess: MutableSharedFlow<Unit> = MutableSharedFlow()
    val _createAlbumSuccess: SharedFlow<Unit> = createAlbumSuccess


    fun updateSelectedIndex(type: Type) {
        this.pageData.value = this._pageData.value.copy(currentSelectTag = type)
    }

    fun updateTitle(title: String) {
        this.pageData.value = this._pageData.value.copy(title = title)
    }

    private fun updateFaceUrl(imageUrl: String) {
        this.pageData.value = this._pageData.value.copy(faceUrl = imageUrl)
    }

    // 上传图片
    fun uploadPicture(base64Str: String, type: String) {
        launchUIWithDialog {
            val targetPic = mainRepository.uploadFile(base64Str, type)
            updateFaceUrl(targetPic.imageUrl)
        }
    }

    // 上传拍照的图片
    fun uploadCameraPic(bitmap: Bitmap, type: String) {
        launchUIWithDialog {
            val base64Str = withContext(Dispatchers.IO) {
                HandlePhoto.compressBitmap2Base64(
                    HandlePhoto.zoomImage(bitmap), 250L
                )
            }
            val targetPic = mainRepository.uploadFile(base64Str, type)
            updateFaceUrl(targetPic.imageUrl)
        }
    }

    // 创建相册
    fun createAlbum() {
        launchUIWithDialog {
            mainRepository.createAlbum(_pageData.value)
            createAlbumSuccess.emit(Unit)
        }
    }
}