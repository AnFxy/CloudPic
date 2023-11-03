package com.fxyandtjh.voiceaccounting.tool

import android.media.MediaRecorder
import android.text.format.DateFormat
import com.blankj.utilcode.util.Utils
import java.lang.Exception
import java.util.Calendar
import java.util.Locale

class VoiceController private constructor() {
    private val mMediaRecorder: MediaRecorder? by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        MediaRecorder(Utils.getApp())
    }
    private var fileName = ""

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            VoiceController()
        }
    }

    fun startRecordVoice() {
        try {
            mMediaRecorder?.run {
                reset()
                // 设置音频来源为麦克风 MIC 麦克风
                setAudioSource(MediaRecorder.AudioSource.MIC)
                // 设置录制音频的格式
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                // 设置音频的编码格式
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                // 设置录制文件所在路径
                fileName = "${DateFormat.format("yyyyMMdd_HHmmss", Calendar.getInstance(Locale.CHINA))}.m4a"

                val audioSaveDir=Utils.getApp().filesDir.absolutePath

                val targetPath ="$audioSaveDir/$fileName"
                setOutputFile(targetPath)
                // 开始录制
                prepare()
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopRecordVoice(callback: (String) -> Unit) {
        try {
            mMediaRecorder?.run {
                // 结束录制， 并返回录制文件的文件名
                stop()
                callback.invoke(fileName)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}