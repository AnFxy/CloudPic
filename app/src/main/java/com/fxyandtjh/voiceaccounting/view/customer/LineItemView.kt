package com.fxyandtjh.voiceaccounting.view.customer

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.fxyandtjh.voiceaccounting.R
import com.fxyandtjh.voiceaccounting.databinding.LineItemCusBinding
import com.fxyandtjh.voiceaccounting.tool.isShow
import com.fxyandtjh.voiceaccounting.tool.setVisible

class LineItemView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private val binding =
        LineItemCusBinding.inflate(LayoutInflater.from(context), this, true)

    var key: String
        get() = binding.tvKey.text.toString()
        set(value) {
            binding.tvKey.text = value
        }

    var value: String
        get() = binding.tvValue.text.toString()
        set(value) {
            binding.tvValue.text = value
            binding.ivArrow.setVisible(value == "")
        }

    var showLine: Boolean
        get() = binding.line.isShow()
        set(value) {
            binding.line.setVisible(value)
        }

    init {
        initAttribute(attrs)
    }

    private fun initAttribute(attrs: AttributeSet?) {
        val style = context.obtainStyledAttributes(attrs, R.styleable.LineItemView)
        key = style.getString(R.styleable.LineItemView_key) ?: ""
        value = style.getString(R.styleable.LineItemView_value) ?: ""
        showLine = style.getBoolean(R.styleable.LineItemView_showLine, true)
        style.recycle()
    }
}