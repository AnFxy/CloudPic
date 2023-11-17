package com.fxyandtjh.voiceaccounting.adapter

import android.view.ViewGroup.LayoutParams
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.fxyandtjh.voiceaccounting.R
import com.fxyandtjh.voiceaccounting.net.response.AlbumInfo
import com.fxyandtjh.voiceaccounting.tool.PicLoadUtil
import com.fxyandtjh.voiceaccounting.tool.dip2px
import com.fxyandtjh.voiceaccounting.tool.getScreenWidth

class AlbumAdapter : BaseQuickAdapter<AlbumInfo, BaseViewHolder>(R.layout.item_album) {
    override fun convert(holder: BaseViewHolder, item: AlbumInfo) {
        holder.setText(R.id.tv_title, item.title)
        holder.setText(R.id.tv_count, "${item.total}")
        holder.setText(R.id.tv_label, getLabelNameById(item.labelId))
        val picView = holder.getView<ImageView>(R.id.iv_album)
        // 设置相册图片大小
        val screenWidth = getScreenWidth()
        val picWidth = (screenWidth / 2) - dip2px(24f)
        picView.layoutParams = LayoutParams(picWidth, picWidth)
        // 加载图片
        // 加载头像
        PicLoadUtil.instance.loadPic(
            context = context,
            url = if (holder.layoutPosition == 0) R.mipmap.new_album else item.faceUrl,
            targetView = picView
        )
        // 如果是第一张需要隐藏
        holder.setVisible(R.id.container_des, holder.layoutPosition != 0)
    }

    private fun getLabelNameById(id: Int) =
        when (id) {
            -1 -> "普通"
            0 -> "多人"
            1 -> "亲子"
            2 -> "旅游"
            3 -> "情侣"
            else -> "普通"
        }
}