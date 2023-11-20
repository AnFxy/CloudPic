package com.fxyandtjh.voiceaccounting.view.customer

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import com.blankj.utilcode.util.Utils
import com.fxyandtjh.voiceaccounting.R
import com.fxyandtjh.voiceaccounting.base.setLimitClickListener
import com.fxyandtjh.voiceaccounting.databinding.LineItemCusBinding
import com.fxyandtjh.voiceaccounting.tool.PicLoadUtil
import com.fxyandtjh.voiceaccounting.tool.setVisible

class LineItemView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private val binding =
        LineItemCusBinding.inflate(LayoutInflater.from(context), this, true)

    var callBackClickHead: (() -> Unit)? = null

    var key: String = ""
        set(value) {
            field = value
            binding.tvKey.text = value
        }

    var value: String = ""
        set(value) {
            field = value
            binding.tvValue.text = value
            updateEditAndIconAndGender(value)
        }

    var showLine: Boolean = true
        set(value) {
            field = value
            binding.line.setVisible(value)
        }

    var type: Int = 4
        set(value) {
            field = value
            updateViewVisible(value)
        }

    var isEnable: Boolean = true
        set(value) {
            field = value
            updateViewEnable(value)
        }

    var allowTypeChange: Boolean = false

    init {
        initAttribute(attrs)
    }

    private fun initAttribute(attrs: AttributeSet?) {
        val style = context.obtainStyledAttributes(attrs, R.styleable.LineItemView)
        type = style.getInt(R.styleable.LineItemView_type, 4)
        allowTypeChange = style.getBoolean(R.styleable.LineItemView_allowTypeChange, false)
        key = style.getString(R.styleable.LineItemView_key) ?: ""
        value = style.getString(R.styleable.LineItemView_value) ?: ""
        showLine = style.getBoolean(R.styleable.LineItemView_showLine, true)
        isEnable = style.getBoolean(R.styleable.LineItemView_isEnable, true)
        style.recycle()

        initView()
    }

    private fun initView() {
        // 昵称编辑
        binding.etEdit.addTextChangedListener {
            val currentText = it?.toString() ?: ""
            if (currentText != value) {
                value = currentText
            }
        }
        // 性别选择
        binding.genderContainer.setOnCheckedChangeListener { _, checkedId ->
            val currentValue = if (checkedId == R.id.gender_male) "1" else "0"
            if (currentValue != value) {
                value = currentValue
            }
        }
        // 头像选择
        binding.ivHead.setLimitClickListener {
            if (isEnable) {
                callBackClickHead?.invoke()
            }
        }
    }

    private fun updateViewVisible(type: Int) {
        binding.ivArrow.setVisible(type == Type.EMPTY.value)
        binding.ivHead.setVisible(type == Type.ICON.value)
        binding.etEdit.setVisible(type == Type.EDIT.value)
        binding.tvValue.setVisible(type == Type.FIXED.value)
        binding.genderContainer.setVisible(type == Type.Gender.value)
    }

    private fun updateEditAndIconAndGender(value: String) {
        if (value != binding.etEdit.text.toString()) {
            binding.etEdit.setText(value)
        }

        if (type == Type.Gender.value) {
            binding.genderMale.isChecked = value == "1"
            binding.genderFemale.isChecked = value == "0"
        }
        if (type == Type.ICON.value) {
            PicLoadUtil.instance.loadPic(
                context = context,
                url = if (value.equals("")) R.mipmap.default_head else value,
                borderWidth = 5f,
                targetView = binding.ivHead
            )
        }
    }

    private fun updateViewEnable(isEnable: Boolean) {
        // Key的颜色
        binding.tvKey.setTextColor(
            Utils.getApp().getColor(
                if (isEnable)
                    R.color.enable_line_color
                else
                    R.color.disable_line_color
            )
        )
        // 昵称编辑
        if (allowTypeChange) {
            type = if (isEnable) Type.EDIT.value else Type.FIXED.value
        }
        // 性别选择是否可用
        binding.genderFemale.isEnabled = isEnable
        binding.genderMale.isEnabled = isEnable
    }

    enum class Type(val value: Int) {
        ICON(1),
        EDIT(2),
        FIXED(3),
        EMPTY(4),
        Gender(5)
    }
}