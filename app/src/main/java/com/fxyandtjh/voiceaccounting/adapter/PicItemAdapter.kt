package com.fxyandtjh.voiceaccounting.adapter

import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.fxyandtjh.voiceaccounting.R
import com.fxyandtjh.voiceaccounting.base.setLimitClickListener
import com.fxyandtjh.voiceaccounting.net.response.PictureInfo
import com.fxyandtjh.voiceaccounting.tool.PicLoadUtil
import com.fxyandtjh.voiceaccounting.tool.dip2px
import com.fxyandtjh.voiceaccounting.tool.getScreenWidth

class PicItemAdapter(private val callBackClick: (PictureInfo) -> Unit) :
    BaseQuickAdapter<PictureInfo, BaseViewHolder>(R.layout.item_pic_item) {
    override fun convert(holder: BaseViewHolder, item: PictureInfo) {
        val picView = holder.getView<ImageView>(R.id.iv_pic)
        // 设置相册图片大小
        val screenWidth = getScreenWidth()
        val picWidth = (screenWidth / 3) - dip2px(18f)
        picView.layoutParams = ViewGroup.LayoutParams(picWidth, picWidth)
        PicLoadUtil.instance.loadPic(
            context = context,
            url = item.imageUrl,
            targetView = picView
        )
        // 点击事件
        holder.getView<ConstraintLayout>(R.id.pic_container).setLimitClickListener {
            callBackClick.invoke(item)
        }
    }
}