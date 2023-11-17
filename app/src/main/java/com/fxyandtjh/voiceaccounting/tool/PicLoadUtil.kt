package com.fxyandtjh.voiceaccounting.tool

import android.content.Context
import android.graphics.Color
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class PicLoadUtil private constructor() {

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            PicLoadUtil()
        }
    }

    fun loadPic(
        context: Context?,
        url: Any,
        radius: Float = 10f,
        borderWidth: Float = 0f,
        borderColor: Int = Color.parseColor("#FFFFFF"),
        targetView: ImageView
    ) {
        val options = RequestOptions()
            .transform(RoundedCorners(dip2px(radius)))

        context?.let {
            var glide = Glide.with(it).apply {
                applyDefaultRequestOptions(options)
            }.load(url)
            if (borderWidth != 0f) {
                glide = glide.transform(PicTransform(borderWidth, borderColor))
            }
            glide.into(targetView)
        }
    }
}