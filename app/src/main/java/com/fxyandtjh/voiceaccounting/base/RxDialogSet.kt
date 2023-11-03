package com.fxyandtjh.voiceaccounting.base

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
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
        window?.setLayout(width, height)
        return this
    }

    fun setCancelByGesture(isAllowed: Boolean = false): RxDialogSet {
        setCancelable(isAllowed)
        setCanceledOnTouchOutside(isAllowed)
        return this
    }

    companion object {
        fun provideDialog(context: Context, layoutId: Int) =
            RxDialogSet(context, R.style.SimpleDialog, layoutId)
                .setCancelByGesture()
                .setDialogWidthHeight()

        fun provideDialogBottom(context: Context, view: Int): RxDialogSet {
            val dialog = provideDialog(context, view)
            val dialogWindow = dialog.window
            val lp = dialogWindow?.attributes
            lp?.gravity = Gravity.BOTTOM
            dialogWindow?.attributes = lp
            return dialog
        }
    }

}