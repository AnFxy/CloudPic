package com.fxyandtjh.voiceaccounting.base

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import com.fxyandtjh.voiceaccounting.R

class RxDialogSet(context: Context, theme: Int, private val itemLayoutId: Int) :
    Dialog(context, theme) {

    init {
        setContentView(itemLayoutId)
    }

    fun <T : View> setViewState(viewId: Int, block: T.() -> Unit): RxDialogSet {
        val view: T = findViewById<T>(viewId)
        block.invoke(view)
        return this
    }

    fun setDialogWidthHeight(
        width: Int = ViewGroup.LayoutParams.MATCH_PARENT,
        height: Int = ViewGroup.LayoutParams.MATCH_PARENT
    ): RxDialogSet {
        // 设置状态栏为黑色
        window?.decorView?.windowInsetsController?.setSystemBarsAppearance(
            APPEARANCE_LIGHT_STATUS_BARS, APPEARANCE_LIGHT_STATUS_BARS
        )
        window?.setLayout(width, height)
        return this
    }

    fun setCancelByGesture(isAllowed: Boolean = false): RxDialogSet {
        setCancelable(isAllowed)
        setCanceledOnTouchOutside(isAllowed)
        return this
    }

    companion object {

        fun provideDialogBottom(context: Context, view: Int): RxDialogSet =
            provideDialog(context, view, true)

        fun provideDialog(context: Context, layoutId: Int, isBottom: Boolean = false): RxDialogSet {
            val dialog = RxDialogSet(context, R.style.SimpleDialog, layoutId)
                .setCancelByGesture()
                .setDialogWidthHeight()
            val dialogWindow = dialog.window
            val lp = dialogWindow?.attributes
            lp?.gravity = if (isBottom) Gravity.BOTTOM else Gravity.CENTER
            dialogWindow?.attributes = lp
            return dialog
        }
    }

}