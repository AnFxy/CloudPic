package com.fxyandtjh.voiceaccounting.tool

import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.chad.library.adapter.base.listener.OnItemClickListener

/**
 * 防止用户多次点击
 */
open class PreventMultiClickListener : View.OnClickListener, OnItemChildClickListener,
    OnItemClickListener, Toolbar.OnMenuItemClickListener {
    private var lastClickTime = 0L

    companion object {
        const val limitTime = 400L
    }

    override fun onClick(v: View?) {
        if (System.currentTimeMillis() - lastClickTime >= limitTime) {
            lastClickTime = System.currentTimeMillis()
            onSafeClick()
        }
    }

    open fun onSafeClick() {}

    open fun onItemSafeClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {}

    open fun onItemChildSafeClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {}

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        if (System.currentTimeMillis() - lastClickTime >= limitTime) {
            lastClickTime = System.currentTimeMillis()
            onItemChildSafeClick(adapter, view, position)
        }
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        if (System.currentTimeMillis() - lastClickTime >= limitTime) {
            lastClickTime = System.currentTimeMillis()
            onItemSafeClick(adapter, view, position)
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if (System.currentTimeMillis() - lastClickTime >= limitTime) {
            lastClickTime = System.currentTimeMillis()
            onSafeClick()
        }
        return true
    }
}
