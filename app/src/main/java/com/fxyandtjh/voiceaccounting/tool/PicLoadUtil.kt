package com.fxyandtjh.voiceaccounting.tool

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.provider.MediaStore
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import java.io.ByteArrayOutputStream
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

    /**
     * 图片质量压缩，采用二分法
     *
     * @param bytes       原图字节流
     * @param maxByteSize 压缩图片最大字节 K为单位 最大500K
     * 大致原理：bitmap.compress(Bitmap.CompressFormat.JPEG, midPosition, byteArrayOutputStream);方法
     * 不断变化第二个参数 来压缩获取ByteArrayOutputStream对象 然后比较ByteArrayOutputStream对象的大小和要压缩的字节大小
     */
    fun compressByQuality(bytes: ByteArray, maxByteSize: Long = 500): ByteArray {
        if (bytes.isEmpty()) return bytes
        //压缩图片最大字节
        val maxSize = maxByteSize * 1024
        //图片字节流的大小
        val srcSize = bytes.size
        //图片字节流的大小 小于等于 压缩图片最大字节 不需要压缩
        if (srcSize <= maxByteSize) return bytes
        //图片字节流的大小 大于 压缩图片最大字节 二分法压缩
        //1.根据传入的图片字节流获取Bitmap
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, srcSize)
        //2.读取100%质量的图片流ByteArrayOutputStream对象
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        //3.result最终的图片字节数
        val result: ByteArray
        //如果 100%质量的图片流ByteArrayOutputStream对象的大小 还比要压缩的大小 小于等于 直接返回100%质量的图片流ByteArrayOutputStream对象
        if (byteArrayOutputStream.size() <= maxSize) {
            result = byteArrayOutputStream.toByteArray()
        } else { //需要压缩
            //4.收回掉100%质量的图片流ByteArrayOutputStream对象
            byteArrayOutputStream.reset()
            //5.获取0%质量的图片流ByteArrayOutputStream对象
            bitmap.compress(Bitmap.CompressFormat.JPEG, 0, byteArrayOutputStream)
            //如果 0%质量的图片流ByteArrayOutputStream对象的大小 还比要压缩的大小 大于等于 直接返回0%质量的图片流ByteArrayOutputStream对象
            if (byteArrayOutputStream.size() < maxSize) { // 二分法寻找最佳质量
                var startPosition = 0
                var endPosition = 100
                var midPosition = 0
                while (startPosition < endPosition) {
                    midPosition = (startPosition + endPosition) / 2
                    byteArrayOutputStream.reset()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, midPosition, byteArrayOutputStream)
                    val len = byteArrayOutputStream.size()
                    if (len.toLong() == maxSize) { //找到位置对应的字节流等于要压缩的字节流 退出while循环
                        break
                    } else if (len > maxSize) { //中间位置的字节流大于要压缩的字节流 在前半部分找
                        endPosition = midPosition - 1
                    } else { //中间位置的字节流小于等于要压缩的字节流 在后半部分找
                        startPosition = midPosition + 1
                    }
                }
                if (endPosition == midPosition - 1) {
                    byteArrayOutputStream.reset()
                    bitmap.compress(
                        Bitmap.CompressFormat.JPEG,
                        startPosition,
                        byteArrayOutputStream
                    )
                }
            }
            result = byteArrayOutputStream.toByteArray()
        }
        //最后回收掉Bitmap
        bitmap.recycle()
        //返回图片字节流
        return result
    }
}
