package com.fxyandtjh.voiceaccounting.view.customer

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.blankj.utilcode.util.Utils
import com.fxyandtjh.voiceaccounting.R
import com.fxyandtjh.voiceaccounting.base.setLimitClickListener
import com.fxyandtjh.voiceaccounting.databinding.AlbumThemeItemBinding


class AlbumThemeItemView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private val binding by lazy {
        AlbumThemeItemBinding.inflate(LayoutInflater.from(context), this, true)
    }

    var callBackClick: (() -> Unit)? = null

    var key: String = ""
        set(value) {
            field = value
            binding.tvTheme.text = key
        }

    var isChecked: Boolean = false
        set(value) {
            field = value
            updateViewStatus(value)
        }

    var isEnable: Boolean = true

    var activeIconId: Int = R.mipmap.default_head

    var inActiveIconId: Int = R.mipmap.default_head

    init {
        initAttribute(attrs)
    }

    private fun initAttribute(attrs: AttributeSet?) {
        val style = context.obtainStyledAttributes(attrs, R.styleable.AlbumThemeItemView)
        key = style.getString(R.styleable.AlbumThemeItemView_key) ?: ""
        isChecked = style.getBoolean(R.styleable.AlbumThemeItemView_isChecked, false)
        activeIconId =
            style.getResourceId(R.styleable.AlbumThemeItemView_activeIconId, R.mipmap.default_head)
        inActiveIconId = style.getResourceId(
            R.styleable.AlbumThemeItemView_inActiveIconId,
            R.mipmap.default_head
        )
        style.recycle()

        initView()
    }

    private fun initView() {
        updateViewStatus(isChecked)
        // 头像选择
        binding.root.setLimitClickListener {
            if (isEnable) {
                callBackClick?.invoke()
            }
        }
    }

    private fun updateViewStatus(value: Boolean) {
        binding.ivTheme.setImageResource(if (value) activeIconId else inActiveIconId)
        binding.tvTheme.setTextColor(
            Utils.getApp().getColor(if (value) R.color.my_blue else R.color.gray)
        )
    }
}