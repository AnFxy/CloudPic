package com.fxyandtjh.voiceaccounting.adapter

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.fxyandtjh.voiceaccounting.R
import com.fxyandtjh.voiceaccounting.net.response.AlbumInfo

class AlbumAdapter : BaseQuickAdapter<AlbumInfo, BaseViewHolder>(R.layout.item_album) {
    override fun convert(holder: BaseViewHolder, item: AlbumInfo) {
        holder.setText(R.id.tv_title, item.title)
        holder.setText(R.id.tv_count, item.total)
        holder.setText(R.id.tv_label, getLabelNameById(item.labelId))
        val options = RequestOptions()
            .transform(RoundedCorners(20))
        Glide.with(holder.itemView).apply {
            applyDefaultRequestOptions(options)
        }.load(item.faceUrl).into(holder.getView<ImageView>(R.id.iv_album))
        holder.setVisible(R.id.container_des, holder.layoutPosition != 0)
    }

    private fun getLabelNameById(id: Int) =
       when(id) {
            -1 -> "普通"
            0 -> "多人"
            1 -> "亲子"
            2 -> "旅游"
            3 -> "情侣"
            else -> "普通"
        }
}