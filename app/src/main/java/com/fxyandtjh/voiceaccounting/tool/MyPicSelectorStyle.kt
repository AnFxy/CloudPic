package com.fxyandtjh.voiceaccounting.tool

import com.blankj.utilcode.util.Utils
import com.fxyandtjh.voiceaccounting.R
import com.luck.picture.lib.style.AlbumWindowStyle
import com.luck.picture.lib.style.BottomNavBarStyle
import com.luck.picture.lib.style.PictureSelectorStyle
import com.luck.picture.lib.style.TitleBarStyle

class MyPicSelectorStyle private constructor() : PictureSelectorStyle() {

    companion object {
        val instance: MyPicSelectorStyle by lazy {
            val tempInfo = MyPicSelectorStyle()
            val tempWindow = AlbumWindowStyle()
            tempWindow.albumAdapterItemTitleColor = Utils.getApp().getColor(R.color.enable_line_color)
            tempWindow.albumAdapterItemBackground = Utils.getApp().getColor(R.color.white)
            tempInfo.albumWindowStyle = tempWindow
            val bottomBar = BottomNavBarStyle()
            bottomBar.bottomNarBarBackgroundColor
            bottomBar.bottomNarBarBackgroundColor = Utils.getApp().getColor(R.color.white)
            tempInfo.bottomBarStyle = bottomBar
            val tempTitle = TitleBarStyle()
            tempTitle.titleTextColor = Utils.getApp().getColor(R.color.enable_line_color)
            tempTitle.titleBackgroundColor = Utils.getApp().getColor(R.color.white)
            tempInfo.titleBarStyle = tempTitle
            tempInfo
        }
    }
}