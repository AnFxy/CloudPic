package com.fxyandtjh.voiceaccounting.adapter

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.fxyandtjh.voiceaccounting.R
import com.fxyandtjh.voiceaccounting.net.response.BugInfo
import com.fxyandtjh.voiceaccounting.tool.timeStampToDate

class BugAdapter : BaseQuickAdapter<BugInfo, BaseViewHolder>(R.layout.item_bug) {

    override fun convert(holder: BaseViewHolder, item: BugInfo) {
        holder.setText(R.id.tv_type, if (item.type == 1) "BUG反馈" else "功能优化");
        holder.setText(R.id.tv_des, item.content)
        val statusText = when (item.status) {
            1 -> "处理中"
            2 -> "已完成(已修复｜已优化)"
            3 -> "已关闭-无需处理"
            else -> "已提交-未处理"
        }
        holder.setText(R.id.tv_status, statusText)
        holder.setText(R.id.tv_pic_count, "${item.images.size}张")
        holder.setText(R.id.tv_create_time, timeStampToDate(item.createTime))

        // 相关截图
        val rvPic = holder.getView<RecyclerView>(R.id.rv_pic)
        val mAdapter = PicBugAdapter(spanCount = 3, isEnable = false)
        rvPic.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
        mAdapter.setNewInstance(item.images.toMutableList())
    }
}