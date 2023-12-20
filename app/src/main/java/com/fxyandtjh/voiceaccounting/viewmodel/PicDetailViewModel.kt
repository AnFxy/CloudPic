package com.fxyandtjh.voiceaccounting.viewmodel

import android.graphics.BitmapFactory
import com.blankj.utilcode.util.Utils
import com.fxyandtjh.voiceaccounting.base.BaseViewModel
import com.fxyandtjh.voiceaccounting.entity.PicsDetail
import com.fxyandtjh.voiceaccounting.tool.PicLoadUtil
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.DownloadResult
import com.github.panpf.sketch.request.execute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import java.io.InputStream
import javax.inject.Inject


@HiltViewModel
class PicDetailViewModel @Inject constructor() : BaseViewModel() {

    var picData: PicsDetail = PicsDetail(
        picList = emptyList(),
        selectedIndex = -1
    )

    var fullScreen: Boolean = false

    private val downLoadEvent: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val _downLoadEvent: SharedFlow<Boolean> = downLoadEvent

    fun updateSelectedIndex(currentIndex: Int) {
        picData = picData.copy(selectedIndex = currentIndex)
    }

    fun downloadPicture(targetUrl: String) {
        launchUIWithDialog {
            val result: DownloadResult =
                DownloadRequest(Utils.getApp().applicationContext, targetUrl).execute()
            if (result is DownloadResult.Success) {
                val inputStream: InputStream = result.data.data.newInputStream()
                val targetBitmap = BitmapFactory.decodeStream(inputStream)
                PicLoadUtil.instance.saveImageToGallery(
                    Utils.getApp().applicationContext,
                    targetBitmap
                )
                downLoadEvent.emit(true)
            } else if (result is DownloadResult.Error) {
                downLoadEvent.emit(false)
            }
        }
    }
}