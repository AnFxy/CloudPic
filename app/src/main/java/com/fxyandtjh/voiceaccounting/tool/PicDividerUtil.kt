package com.fxyandtjh.voiceaccounting.tool

import com.fxyandtjh.voiceaccounting.entity.ExtraPictureInfo

class PicDividerUtil private constructor() {

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            PicDividerUtil()
        }
    }

    fun dividerPicData(origPicList: List<ExtraPictureInfo>): List<Pair<Long, List<ExtraPictureInfo>>> {
        // 按时间分组 并倒叙排列
        return origPicList.groupBy { it.pictureInfo.createTime / 86400000 }
            .toList().sortedByDescending { pair -> pair.first }
    }
}
