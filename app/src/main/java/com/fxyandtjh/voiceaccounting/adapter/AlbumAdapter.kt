package com.fxyandtjh.voiceaccounting.adapter

import android.view.ViewGroup.LayoutParams
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.fxyandtjh.voiceaccounting.R
import com.fxyandtjh.voiceaccounting.base.setLimitClickListener
import com.fxyandtjh.voiceaccounting.net.response.AlbumInfo
import com.fxyandtjh.voiceaccounting.tool.PicLoadUtil
import com.fxyandtjh.voiceaccounting.tool.dip2px
import com.fxyandtjh.voiceaccounting.tool.getScreenWidth

class AlbumAdapter(private val callBackClick: (AlbumInfo) -> Unit) :
    BaseQuickAdapter<AlbumInfo, BaseViewHolder>(R.layout.item_album) {
    override fun convert(holder: BaseViewHolder, item: AlbumInfo) {
        holder.setText(R.id.tv_title, item.title)
        holder.setText(R.id.tv_count, "${item.total}张")
        holder.setText(R.id.tv_label, getLabelNameById(item.labelId))
        val picView = holder.getView<ImageView>(R.id.iv_album)
        // 设置相册图片大小
        val screenWidth = getScreenWidth()
        val picWidth = (screenWidth / 2) - dip2px(31f)
        picView.layoutParams = LayoutParams(picWidth, picWidth)
        // 加载图片
        val targetUrl = if (holder.layoutPosition == 0) {
            R.mipmap.new_album
        } else if (item.faceUrl == "") {
            R.mipmap.default_album
        } else {
            item.faceUrl
        }
        PicLoadUtil.instance.loadPic(
            context = context,
            url = targetUrl,
            targetView = picView
        )
        // 如果是第一张需要隐藏
        holder.setVisible(R.id.container_des, holder.layoutPosition != 0)
        // 如果是第一张需要设置背景灰色， 其他白色
        holder.itemView.setBackgroundResource(
            if (holder.layoutPosition == 0)
                R.drawable.bg_10_gray
            else
                R.drawable.bg_10_white
        )
        // 点击事件
        holder.getView<ConstraintLayout>(R.id.album_container).setLimitClickListener {
            callBackClick.invoke(item)
        }
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