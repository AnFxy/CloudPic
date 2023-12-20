package com.fxyandtjh.voiceaccounting.viewmodel

import android.graphics.Bitmap
import com.fxyandtjh.voiceaccounting.base.BaseViewModel
import com.fxyandtjh.voiceaccounting.local.LocalCache
import com.fxyandtjh.voiceaccounting.net.response.UserInfo
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
class UserInfoViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : BaseViewModel() {

    private val editAble: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val _editAble: StateFlow<Boolean> = editAble

    private val pageData: MutableStateFlow<UserInfo> = MutableStateFlow(LocalCache.userInfo)
    val _pageData: StateFlow<UserInfo> = pageData

    private val updateEvent: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val _updateEvent: SharedFlow<Boolean> = updateEvent

    fun updateEditAbleStatus() {
        editAble.value = !_editAble.value
    }

    fun updateEditDataToVM(des: String, nickName: String, gender: String) {
        pageData.value = _pageData.value.copy(des = des, name = nickName, gender = gender.toInt())
    }

    // 更新用户个人信息
    fun uploadPageDataToRemote(des: String, nickName: String, gender: String) {
        updateEditDataToVM(des, nickName, gender)
        launchUIWithDialog {
            mainRepository.updateUserInformation(_pageData.value)
            updateEvent.emit(true)
        }
    }

    // 上传图片
    fun uploadPicture(base64Str: String, type: String) {
        launchUIWithDialog {
            val targetPic = mainRepository.uploadFile(base64Str, type)
            pageData.value = _pageData.value.copy(
                headUrl = targetPic.imageUrl
            )
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
            pageData.value = _pageData.value.copy(
                headUrl = targetPic.imageUrl
            )
        }
    }
}