package com.fxyandtjh.voiceaccounting.tool

import android.view.View

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
