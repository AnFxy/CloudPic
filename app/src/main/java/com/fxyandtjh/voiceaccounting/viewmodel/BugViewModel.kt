package com.fxyandtjh.voiceaccounting.viewmodel

import com.fxyandtjh.voiceaccounting.base.BaseViewModel
import com.fxyandtjh.voiceaccounting.net.response.BugInfo
import com.fxyandtjh.voiceaccounting.repository.impl.MainRepository
import com.fxyandtjh.voiceaccounting.repository.impl.StartupRepository
import com.fxyandtjh.voiceaccounting.tool.isEmailValid
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class BugViewModel @Inject constructor(
    private val startupRepository: StartupRepository,
    private val mainRepository: MainRepository
) : BaseViewModel() {

    var currentIndex: Int = 0

    private val pageData: MutableStateFlow<BugInfo> = MutableStateFlow(
        BugInfo(
            type = 1,
            content = "",
            images = emptyList(),
            email = "",
            status = 0,
            createTime = 0
        )
    )
    val _pageData: StateFlow<BugInfo> = pageData

    // 关闭页面 0 正常关闭、 1 邮箱填写不合法、2 内容描述不合法
    private val goBackEvent: MutableSharedFlow<Int> = MutableSharedFlow()
    val _goBackEvent: SharedFlow<Int> = goBackEvent

    fun updatePage(content: String, email: String) {
        pageData.value = _pageData.value.copy(
            content = content,
            email = email
        )
    }

    fun updatePage(type: Int) {
        pageData.value = _pageData.value.copy(type = type)
    }

    // 上传图片
    fun uploadPicture(base64Str: String, type: String) {
        launchUIWithDialog {
            val targetPic = mainRepository.uploadFile(base64Str, type)
            // 原先的图片列表
            val targetImages = _pageData.value.images.toMutableList()
            // 分为两种情况，一种是新增，一种是替换
            if (currentIndex >= targetImages.size) {
                targetImages.add(targetPic.imageUrl)
            } else {
                targetImages[currentIndex] = targetPic.imageUrl
            }
            pageData.value = _pageData.value.copy(
                images = targetImages
            )
        }
    }

    // 移除照片
    fun removePicture() {
        if (currentIndex >= 0 && currentIndex <= _pageData.value.images.size) {
            // 原先的图片列表
            val targetImages = _pageData.value.images.toMutableList()
            targetImages.removeAt(currentIndex)
            pageData.value = _pageData.value.copy(
                images = targetImages
            )
        }
    }

    // 上传用户数据
    fun commitBug() {
        // 校验邮箱是否合法且存在
        if (_pageData.value.email.isEmpty() || !_pageData.value.email.isEmailValid()) {
            launchUI {
                goBackEvent.emit(1)
            }
            return
        }
        // 校验内容是否为空
        if (_pageData.value.content.isEmpty()) {
            launchUI {
                goBackEvent.emit(2)
            }
            return
        }
        launchUIWithDialog {
            startupRepository.uploadBugs(bugInfo = _pageData.value)
            pageData.value = BugInfo(
                type = 1,
                content = "",
                images = emptyList(),
                email = "",
                status = 0,
                createTime = 0
            )
            goBackEvent.emit(0)
        }
    }
}