package com.fxyandtjh.voiceaccounting.tool

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.huantansheng.easyphotos.engine.ImageEngine

class GlideEngine private constructor() : ImageEngine {
    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            GlideEngine()
        }
    }

    override fun loadPhoto(context: Context, uri: Uri, imageView: ImageView) {
        Glide.with(context).load(uri).transition(withCrossFade()).into(imageView)
    }

    override fun loadGifAsBitmap(context: Context, gifUri: Uri, imageView: ImageView) {
        Glide.with(context).asBitmap().load(gifUri).into(imageView)
    }

    override fun loadGif(context: Context, gifUri: Uri, imageView: ImageView) {
        Glide.with(context).asGif().load(gifUri).transition(withCrossFade()).into(imageView)
    }

    override fun getCacheBitmap(context: Context, uri: Uri, width: Int, height: Int): Bitmap =
        Glide.with(context).asBitmap().load(uri).submit(width, height).get()
}
