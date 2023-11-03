package com.fxyandtjh.voiceaccounting.base

import android.view.View
import androidx.appcompat.widget.Toolbar
import com.fxyandtjh.voiceaccounting.tool.PreventMultiClickListener

fun View.setLimitClickListener(callback: () -> Unit) {
    setOnClickListener(object : PreventMultiClickListener(){
        override fun onSafeClick() {
            callback.invoke()
        }
    })
}

fun Toolbar.setLimitMenuClickListener(back: () -> Unit, menu: () -> Unit) {
    setNavigationOnClickListener(object : PreventMultiClickListener() {
        override fun onSafeClick() {
            back.invoke()
        }
    })
    setOnMenuItemClickListener(object : PreventMultiClickListener() {
        override fun onSafeClick() {
            menu.invoke()
        }
    })
}