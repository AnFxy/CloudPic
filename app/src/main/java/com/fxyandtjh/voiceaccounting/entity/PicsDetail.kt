package com.fxyandtjh.voiceaccounting.entity

import java.io.Serializable

data class PicsDetail(
    val selectedIndex: Int,
    val picList: List<ExtraPictureInfo>
) : Serializable