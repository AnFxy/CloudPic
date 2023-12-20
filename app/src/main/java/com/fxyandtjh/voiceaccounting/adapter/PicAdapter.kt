package com.fxyandtjh.voiceaccounting.adapter

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.fxyandtjh.voiceaccounting.R
import com.fxyandtjh.voiceaccounting.net.response.PictureInfo
import com.fxyandtjh.voiceaccounting.tool.CustomItemDecoration
import com.fxyandtjh.voiceaccounting.tool.dip2px
import com.fxyandtjh.voiceaccounting.tool.timeStampToAlbumDate

class PicAdapter(private val callBackClick: (PictureInfo) -> Unit) :
    BaseQuickAdapter<Pair<Long, List<PictureInfo>>, BaseViewHolder>(R.layout.item_pic) {
    override fun convert(holder: BaseViewHolder, item: Pair<Long, List<PictureInfo>>) {
        holder.setText(R.id.tv_time, timeStampToAlbumDate(item.first * 86400000))

        val rvTarget = holder.getView<RecyclerView>(R.id.rv_pic_item)
        val mAdapter = PicItemAdapter {
            callBackClick.invoke(it)
        }
        rvTarget.apply {
            layoutManager = GridLayoutManager(context, 3)
            addItemDecoration(CustomItemDecoration(dip2px(20f)))
            adapter = mAdapter
        }
        mAdapter.setNewInstance(item.second.toMutableList())
    }
}