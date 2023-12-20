package com.fxyandtjh.voiceaccounting.tool

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


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
            .transform()

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

    fun saveImageToGallery(context: Context, bmp: Bitmap) {
        // 首先保存图片
        val storePath =
           context.getExternalFilesDir("image/*")?.absolutePath + File.separator + "CloudAlbum" + File.separator
        val appDir = File(storePath)
        if (!appDir.exists()) {
            appDir.mkdir()
        }
        val fileName = "image_" + System.currentTimeMillis() + ".jpg"
        val file = File(appDir, fileName)
        try {
            val fos = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(
                context.contentResolver,
                file.absolutePath,
                fileName,
                null
            )
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        // 最后通知图库更新
        context.sendBroadcast(
            Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse(file.absolutePath)
            )
        )
    }
}