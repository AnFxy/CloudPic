package com.fxyandtjh.voiceaccounting.entity

import com.fxyandtjh.voiceaccounting.net.response.PictureInfo
import java.io.Serializable

data class ExtraPictureInfo(
    val pictureInfo: PictureInfo,
    val showSelect: Boolean = false,
    val selected: Boolean = false
) : Serializable
