package com.fxyandtjh.voiceaccounting.entity

import com.fxyandtjh.voiceaccounting.net.response.PictureInfo
import java.io.Serializable

data class PicsDetail(
    val selectedIndex: Int,
    val picList: List<PictureInfo>
) : Serializable