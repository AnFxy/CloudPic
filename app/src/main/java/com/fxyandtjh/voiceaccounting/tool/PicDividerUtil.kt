package com.fxyandtjh.voiceaccounting.tool

import com.fxyandtjh.voiceaccounting.net.response.PictureInfo

class PicDividerUtil private constructor() {

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            PicDividerUtil()
        }
    }

    fun dividerPicData(origPicList: List<PictureInfo>): List<Pair<Long, List<PictureInfo>>> {
        // 按时间分组 并倒叙排列
        return origPicList.groupBy { it.createTime / 86400000 }
            .toList().sortedByDescending { pair -> pair.first }
    }
}
