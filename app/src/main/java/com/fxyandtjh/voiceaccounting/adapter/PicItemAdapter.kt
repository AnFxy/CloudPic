package com.fxyandtjh.voiceaccounting.adapter

import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.fxyandtjh.voiceaccounting.R
import com.fxyandtjh.voiceaccounting.base.setLimitClickListener
import com.fxyandtjh.voiceaccounting.entity.ExtraPictureInfo
import com.fxyandtjh.voiceaccounting.tool.PicLoadUtil
import com.fxyandtjh.voiceaccounting.tool.dip2px
import com.fxyandtjh.voiceaccounting.tool.getScreenWidth
import com.fxyandtjh.voiceaccounting.tool.setVisible

class PicItemAdapter(
    private val callBackClick: (ExtraPictureInfo) -> Unit,
    private val callBackSelect: (ExtraPictureInfo, Boolean) -> Unit
) :
    BaseQuickAdapter<ExtraPictureInfo, BaseViewHolder>(R.layout.item_pic_item) {
    override fun convert(holder: BaseViewHolder, item: ExtraPictureInfo) {
        val picView = holder.getView<ImageView>(R.id.iv_pic)
        // 设置相册图片大小
        val screenWidth = getScreenWidth()
        val picWidth = (screenWidth / 3) - dip2px(18f)
        picView.layoutParams = ViewGroup.LayoutParams(picWidth, picWidth)
        PicLoadUtil.instance.loadPic(
            context = context,
            url = item.pictureInfo.imageUrl,
            targetView = picView
        )
        picView.alpha = if (item.selected) 0.5F else 1F
        // 点击事件
        holder.getView<ConstraintLayout>(R.id.pic_container).setLimitClickListener {
            callBackClick.invoke(item)
        }
        // 设置选择
        holder.getView<CheckBox>(R.id.cb_select).apply {
            setVisible(item.showSelect)
            if (item.selected != isChecked) {
                isChecked = item.selected
            }
            setOnCheckedChangeListener { _, isChecked ->
                callBackSelect.invoke(item, isChecked)
            }
        }
    }
}