package com.fxyandtjh.voiceaccounting.tool

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.text.TextUtils
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.FileProvider
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.PermissionUtils.SimpleCallback
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.Utils
import com.fxyandtjh.voiceaccounting.R
import com.fxyandtjh.voiceaccounting.entity.ApkProgress
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL

class DownLoadApk private constructor() {
    private lateinit var builder: NotificationCompat.Builder
    private lateinit var notification: Notification
    private lateinit var notificationManager: NotificationManager
    private var lastUpdateNoticeTime = 0L

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            DownLoadApk()
        }
    }

    fun download(url: String, callBack: (ApkProgress) -> Unit) {
        PermissionUtils.permission(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ).callback(object : SimpleCallback {
            override fun onGranted() {
                startDownLoad(url, callBack)
            }

            override fun onDenied() {
                ToastUtils.showShort(Utils.getApp().getText(R.string.deny_permission_update))
            }

        }).request()

    }

    // 开始下载
    private fun startDownLoad(url: String, callBack: (ApkProgress) -> Unit) {
        if (TextUtils.isEmpty(url)) {
            return
        }
        // 获取文件名
        val fileName = File(URL(url).path).name
        if (TextUtils.isEmpty(fileName)) {
            return
        }
        // 创建通知
        createNotification()
        // 下载外部公有目录： /storage/emulated/0/Android/data/包名/files/Download/
        val targetFile =
            Utils.getApp()
                .getExternalFilesDir("${Environment.DIRECTORY_DOWNLOADS}${File.separator}$fileName")
        // 如果这个目录下 已经有同名文件了 那么要移除文件
        targetFile?.let {
            if (it.exists()) {
                it.delete()
            }
        }

        val a  = targetFile?.absolutePath?.replace(fileName, "") ?: ""

        Downloader(a, fileName)
            .downLoad(url, { curBytes, totalBytes ->
                callBack.invoke(ApkProgress(bytes = curBytes, totalBytes = totalBytes))
                // 更新通知进度
                updateNotificationProgress(curBytes, totalBytes)
            }, { file ->
                callBack.invoke(ApkProgress(path = file.path))
                //  点击通知进行安装
                installAPKWhenNoticeClicked(file)
            }, { _ ->
                callBack.invoke(ApkProgress(code = 400))
            })
    }

    // 生成通知栏
    private fun createNotification() {
        notificationManager =
            Utils.getApp().getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        // 获取渠道ID
        val channel = NotificationChannel(
            "app_channel_ID",
            "app_channel_NAME",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
        // 获取应用图标
        val packageName = AppUtils.getAppPackageName()
        var iconId = 0
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                iconId = Utils.getApp().packageManager
                    .getApplicationInfo(packageName, PackageManager.ApplicationInfoFlags.of(0)).icon
            } else {
                iconId = Utils.getApp().packageManager
                    .getApplicationInfo(packageName, 0).icon
            }
        } catch (e: Exception) {
            e.printStackTrace()
            iconId = R.mipmap.logo
        }
        builder = NotificationCompat.Builder(Utils.getApp(), "app_channel_ID")
            .setSmallIcon(iconId)
            .setContentTitle("新版本更新")
            .setContentText("正在下载...")
            .setProgress(100, 0, false)
        notification = builder.build()
        notificationManager.notify(1, notification)
    }

    // 更新通知进度
    private fun updateNotificationProgress(curBytes: Long, totalBytes: Long) {
        var tipText = ""
        if (curBytes == totalBytes && totalBytes != 0L) {
            // 下载完成
            tipText = "下载完成(点击安装)。"
            builder.setProgress(0, 0, false)
        } else if (totalBytes == 0L) {
            tipText = "正在下载： 0%"
            builder.setProgress(100, 0, false)
        } else {
            val progress = curBytes * 100 / totalBytes
            tipText = "正在下载：$progress%"
            builder.setProgress(100, progress.toInt(), false)
        }
        builder.setContentText(tipText)
        // 下载过程中，进度反馈会非常频繁，频繁更新通知将会消耗大量内存 这里做一个限制 每250毫秒更新一次进度
        limitUpdateNotificationRate(curBytes != totalBytes && totalBytes != 0L)
    }

    // 限制更新通知进度的频率
    private fun limitUpdateNotificationRate(shouldLimit: Boolean) {
        val currentTime = System.currentTimeMillis()
        if (!shouldLimit || currentTime - lastUpdateNoticeTime > 250) {
            // 250 毫秒更新一次
            lastUpdateNoticeTime = currentTime
            notificationManager.notify(1, builder.build())
        }
    }

    // 安装应用程序
    private fun installAPKWhenNoticeClicked(file: File, clickNotice: Boolean = false) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val uri = FileProvider.getUriForFile(
            Utils.getApp(),
            AppUtils.getAppPackageName() + ".fileprovider",
            file
        )
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        val stackBuilder = TaskStackBuilder.create(Utils.getApp())
        stackBuilder.addNextIntentWithParentStack(intent)
        val resultPendingIntent = stackBuilder.getPendingIntent(
            0,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        builder.setContentIntent(resultPendingIntent)
        builder.setAutoCancel(true)
        notificationManager.notify(1, builder.build())
    }

    // 内部下载类
    private class Downloader(val targetPath: String, val targetName: String) {

        fun downLoad(
            url: String,
            onProgress: (Long, Long) -> Unit,
            onFinished: (File) -> Unit,
            onError: (Exception) -> Unit
        ) {
            val client = OkHttpClient.Builder().protocols(listOf(Protocol.HTTP_1_1)).build()
            val request = Request.Builder().url(url).get().build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    onError.invoke(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val file = saveToFile(response) { curByte, totalByte ->
                        onProgress.invoke(curByte, totalByte)
                    }
                    file?.let {
                        if (it.exists()) {
                            onFinished(it)
                        } else {
                            onError(RuntimeException("Failed to save file"))
                        }
                    } ?: onError(RuntimeException("Failed to save file"))
                }
            })
        }

        private fun saveToFile(
            response: Response,
            onProgress: (Long, Long) -> Unit
        ): File? {
            if (TextUtils.isEmpty(targetPath) || TextUtils.isEmpty(targetName) || response.body == null) {
                return null
            }
            // 设置缓冲大小
            val buff = ByteArray(40960)
            var len = 0
            val inputStream = response.body!!.byteStream()
            val total: Long = response.body!!.contentLength()
            var sum = 0L
            val dir = File(targetPath)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val targetFile = File(dir, targetName)
            val fileOutputStream = FileOutputStream(targetFile)
            try {
                while (inputStream.read(buff).also { len = it } != -1) {
                    sum += len
                    fileOutputStream.write(buff, 0, len)
                    onProgress(sum, total)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            fileOutputStream.flush()
            fileOutputStream.close()
            inputStream.close()
            return targetFile
        }
    }
}