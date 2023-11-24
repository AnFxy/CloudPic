package com.fxyandtjh.voiceaccounting.adapter

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.fxyandtjh.voiceaccounting.R
import com.fxyandtjh.voiceaccounting.tool.dip2px
import com.fxyandtjh.voiceaccounting.tool.getScreenWidth
import com.fxyandtjh.voiceaccounting.tool.setVisible
import com.fxyandtjh.voiceaccounting.view.customer.PicItemView

class PicBugAdapter(val spanCount: Int, val isEnable: Boolean = true) :
    BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_rv_bug) {
    var callBackClickPic: ((Int) -> Unit)? = null
    var callBackClickTrashCan: ((Int) -> Unit)? = null

    override fun convert(holder: BaseViewHolder, item: String) {

        val picView = holder.getView<PicItemView>(R.id.item_pic)
        val spaceView = holder.getView<View>(R.id.div_space)
        // 设置间距
        spaceView.setVisible(holder.layoutPosition != spanCount - 1)
        // 设置相册图片大小
        val screenWidth = getScreenWidth()
        val picWidth = (screenWidth - dip2px(80f)) / spanCount
        picView.size = picWidth
        picView.isEnable = isEnable
        picView.url = item

        // 设置点击事件反馈
        picView.callBackClickPic = {
            callBackClickPic?.invoke(holder.layoutPosition)
        }
        picView.callBackClickTrashCan = {
            callBackClickTrashCan?.invoke(holder.layoutPosition)
        }
    }
}
