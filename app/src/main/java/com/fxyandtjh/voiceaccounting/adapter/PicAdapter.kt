package com.fxyandtjh.voiceaccounting.adapter

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.fxyandtjh.voiceaccounting.R
import com.fxyandtjh.voiceaccounting.entity.ExtraPictureInfo
import com.fxyandtjh.voiceaccounting.tool.CustomItemDecoration
import com.fxyandtjh.voiceaccounting.tool.dip2px
import com.fxyandtjh.voiceaccounting.tool.timeStampToAlbumDate

class PicAdapter(
    private val callBackClick: (ExtraPictureInfo) -> Unit,
    private val callBackSelect: (ExtraPictureInfo, Boolean) -> Unit
) :
    BaseQuickAdapter<Pair<Long, List<ExtraPictureInfo>>, BaseViewHolder>(R.layout.item_pic) {

    private val itemDecoration: CustomItemDecoration by lazy {
        CustomItemDecoration(dip2px(20f))
    }

    override fun convert(holder: BaseViewHolder, item: Pair<Long, List<ExtraPictureInfo>>) {
        holder.setText(R.id.tv_time, timeStampToAlbumDate(item.first * 86400000))

        val rvTarget = holder.getView<RecyclerView>(R.id.rv_pic_item)
        val mAdapter = PicItemAdapter({
            callBackClick.invoke(it)
        }, { item, isChecked ->
            callBackSelect.invoke(item, isChecked)
        })
        rvTarget.removeItemDecoration(itemDecoration)
        rvTarget.apply {
            layoutManager = GridLayoutManager(context, 3)
            addItemDecoration(itemDecoration)
            adapter = mAdapter
        }
        mAdapter.setNewInstance(item.second.toMutableList())
    }
}