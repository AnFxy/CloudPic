package com.fxyandtjh.voiceaccounting.adapter

import android.graphics.Color
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.fxyandtjh.voiceaccounting.R
import com.fxyandtjh.voiceaccounting.base.setLimitClickListener
import com.fxyandtjh.voiceaccounting.entity.SecurityQuestion

class QuestionAdapter(private val callBackClick: (SecurityQuestion) -> Unit) :
    BaseQuickAdapter<SecurityQuestion, BaseViewHolder>(R.layout.item_question) {

    var selectID: Int = -1
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun convert(holder: BaseViewHolder, item: SecurityQuestion) {
        holder.getView<TextView>(R.id.tv_question).apply {
            text = item.question
            setTextColor(
                if (selectID == item.id)
                    Color.parseColor("#13227A")
                else
                    Color.parseColor("#777777")
            )
        }
        holder.setVisible(R.id.iv_check, selectID == item.id)
        holder.itemView.apply {
            setLimitClickListener {
                if (selectID != item.id) {
                    callBackClick.invoke(item)
                }
            }
            setBackgroundResource(
                if (selectID == item.id) {
                    R.drawable.bg_tran_blue_h
                } else {
                    R.drawable.bg_transparent
                }
            )
        }
    }
}
