package com.fxyandtjh.voiceaccounting.tool

import android.content.Context
import android.view.View
import android.view.WindowManager
import com.blankj.utilcode.util.Utils

fun View.setVisible(isCouldSeen: Boolean) {
    visibility = if (isCouldSeen) View.VISIBLE else View.GONE
}

fun View.setVisibleWithUnVisual(isCouldSeen: Boolean) {
    visibility = if (isCouldSeen) View.VISIBLE else View.INVISIBLE
}

fun View.setCantBeSeen() {
    visibility = View.GONE
}

fun View.setCanBeSeen() {
    visibility = View.VISIBLE
}

fun View.isShow(): Boolean = visibility == View.VISIBLE

fun getScreenWidth() =
    (Utils.getApp().applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
        .currentWindowMetrics.bounds.width()

fun dip2px(dpValue: Float): Int {
    val scale = Utils.getApp().applicationContext.resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}