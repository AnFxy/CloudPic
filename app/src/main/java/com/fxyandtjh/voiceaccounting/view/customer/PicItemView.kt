package com.fxyandtjh.voiceaccounting.view.customer

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.fxyandtjh.voiceaccounting.R
import com.fxyandtjh.voiceaccounting.base.setLimitClickListener
import com.fxyandtjh.voiceaccounting.databinding.ItemBugPicBinding
import com.fxyandtjh.voiceaccounting.tool.PicLoadUtil
import com.fxyandtjh.voiceaccounting.tool.setVisible

class PicItemView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private val binding =
        ItemBugPicBinding.inflate(LayoutInflater.from(context), this, true)

    var callBackClickTrashCan: (() -> Unit)? = null

    var callBackClickPic: (() -> Unit)? = null

    var url: String = ""
        set(value) {
            field = value
            loadPic(url)
        }

    var isEnable: Boolean = true

    var size: Int = 80
        set(value) {
            field = value
            resizePic(value)
        }

    init {
        initAttribute(attrs)
    }

    private fun initAttribute(attrs: AttributeSet?) {
        val style = context.obtainStyledAttributes(attrs, R.styleable.PicItemView)
        url = style.getString(R.styleable.PicItemView_url) ?: ""
        isEnable = style.getBoolean(R.styleable.PicItemView_isEnable, true)
        size = style.getInt(R.styleable.PicItemView_size, 80)
        style.recycle()
        initView()
    }

    private fun initView() {
        binding.ivPic.setLimitClickListener {
            if (isEnable) {
                // 启动相册
                callBackClickPic?.invoke()
            }
        }

        binding.ivRemove.setLimitClickListener {
            if (isEnable) {
                // 点击了垃圾桶
                callBackClickTrashCan?.invoke()
            }
        }
    }

    private fun loadPic(url: String) {
        PicLoadUtil.instance.loadPic(
            context = context,
            url = if (url == "") R.mipmap.upload_pic else url,
            radius = 4f,
            targetView = binding.ivPic
        )
        binding.ivRemove.setVisible(url != "" && isEnable)
    }

    private fun resizePic(size: Int) {
        binding.ivPic.layoutParams = LayoutParams(size, size)
        invalidate()
    }
}
