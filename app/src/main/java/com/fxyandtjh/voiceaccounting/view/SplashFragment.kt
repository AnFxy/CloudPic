package com.fxyandtjh.voiceaccounting.view

import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.AppUtils
import com.fxyandtjh.voiceaccounting.BuildConfig
import com.fxyandtjh.voiceaccounting.R
import com.fxyandtjh.voiceaccounting.base.BaseFragment
import com.fxyandtjh.voiceaccounting.base.Constants
import com.fxyandtjh.voiceaccounting.base.RxDialogSet
import com.fxyandtjh.voiceaccounting.base.setLimitClickListener
import com.fxyandtjh.voiceaccounting.databinding.FragSplashBinding
import com.fxyandtjh.voiceaccounting.local.LocalCache
import com.fxyandtjh.voiceaccounting.tool.setVisible
import com.fxyandtjh.voiceaccounting.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : BaseFragment<SplashViewModel, FragSplashBinding>() {
    private val viewModel: SplashViewModel by viewModels()
    private val GB_SIZE = 1048576
    private val MB_SIZE = 1024
    private val MINITE_SIZE = 60
    private val HOUR_SIZE = 3600
    private val DAY_SIZE = 86400

    private val updateDialog: RxDialogSet? by lazy {
        context?.let {
            RxDialogSet.provideDialog(it, R.layout.dia_update)
        }
    }

    override fun getViewMode(): SplashViewModel = viewModel

    override fun getViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): FragSplashBinding = FragSplashBinding.inflate(inflater, parent, false)

    override fun setLayout() {
        // 设置版本号
        binding.tvVersion.text = "V ${BuildConfig.VERSION_NAME}"
    }

    override fun setObserver() {
        // 更新弹框
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel._showUpdate.collect {
                if (BuildConfig.VERSION_CODE >= it.versionCode) {
                    // 不需要更新
                    viewModel.sendGoHomeEvent()
                    return@collect
                }
                updateDialog?.setViewState<TextView>(R.id.tv_version_name) {
                    text = "V${it.versionName}"
                }?.setViewState<TextView>(R.id.tv_title) {
                    text = it.title
                }?.setViewState<TextView>(R.id.tv_content) {
                    text = it.content
                }?.setViewState<LinearLayout>(R.id.container_update) {
                    setLimitClickListener {
                        // 根据下载链接启动 系统浏览器下载
                        if (it.downloadLink != null) {
                            viewModel.startUpdateApp(it.downloadLink)
                        }
                    }
                }?.setViewState<TextView>(R.id.tv_close) {
                    setVisible(BuildConfig.VERSION_CODE >= it.miniVersionCode)
                    setLimitClickListener {
                        // 直接关闭 弹框并 走之后的流程
                        updateDialog?.dismiss()
                        viewModel.sendGoHomeEvent()
                    }
                }
                updateDialog?.show()
            }
        }
        // 更新下载进度
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel._apkProgress.collect { apkProgress ->
                apkProgress?.let {
                    // 判断是否下载完成
                    if (apkProgress.path != "") {
                        AppUtils.installApp(apkProgress.path)
                        return@let
                    }
                    // 进度
                    val progressValue =
                        if (it.totalBytes == 0L) 0 else it.bytes * 100 / it.totalBytes
                    // 当前下载量
                    val currentDownLoadText = formatByteToText(it.bytes)
                    // 总需下载量
                    val totalDownLoadText = formatByteToText(it.totalBytes)
                    updateDialog?.setViewState<LinearLayout>(R.id.btn_container) {
                        setVisible(false)
                    }?.setViewState<LinearLayout>(R.id.progress_container) {
                        setVisible(true)
                    }?.setViewState<ProgressBar>(R.id.progress_bar) {
                        setProgress(progressValue.toInt(), true)
                    }?.setViewState<TextView>(R.id.tv_download) {
                        text = "${currentDownLoadText}/${totalDownLoadText}"
                    }?.setViewState<TextView>(R.id.tv_progress) {
                        text = "${progressValue}%"
                    }
                } ?: updateDialog?.setViewState<LinearLayout>(R.id.btn_container) {
                    setVisible(true)
                }?.setViewState<LinearLayout>(R.id.progress_container) {
                    setVisible(false)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel._startCalculateSpeed.collect {
                calculateDownLoadSpeed()
            }
        }

        // 倒计时
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel._goHomePage.collect {
                binding.ivProgress.setVisible(true)
                for (i in 2 downTo 0) {
                    binding.ivProgress.text = "${i}s"
                    if (i != 0) {
                        delay(1000)
                    } else if (LocalCache.loginType == Constants.ACCOUNT_PASSWORD_LOGIN && LocalCache.isLogged) {
                        // 账号密码登录了，需要进入到首页
                        navController.navigate(SplashFragmentDirections.actionSplashFragmentToMainNavigation())
                    } else if (LocalCache.loginType == Constants.QQ_LOGOIN) {
                        // 如果是QQ登录类型， 要校验QQ登录
                        checkQQLoginStatus()
                    } else {
                        // 进入到登录页面
                        navController.navigate(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
                    }
                }
            }
        }

        binding.ivProgress.setLimitClickListener {
            if (LocalCache.isLogged) {
                // 登录了，需要进入到首页
                navController.navigate(SplashFragmentDirections.actionSplashFragmentToMainNavigation())
            } else {
                // 进入到登录页面
                navController.navigate(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
            }
        }
    }

    private fun checkQQLoginStatus() {
        viewLifecycleOwner.lifecycleScope.launch {
            val qqStatusCode = (activity as MainActivity).doQQLogin()
            if (qqStatusCode == Constants.QQ_LOING_STATUS_OKAY) {
                // 进入到首页
                navController.navigate(SplashFragmentDirections.actionSplashFragmentToMainNavigation())
            } else {
                // 如果QQ状态异常，则清理本地登录相关的缓存
                LocalCache.clearALLCache()
                // 前往登录页面
                navController.navigate(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
            }
        }
    }

    private fun formatByteToText(bytes: Long, bUnit: Boolean = false): String {
        val kbValue = bytes / 1000
        if (kbValue == 0L) {
            return "${bytes}${if (bUnit) "b/s" else "B"}"
        } else if (kbValue < MB_SIZE) {
            return "${kbValue}${if (bUnit) "Kb/s" else "K"}"
        } else if (kbValue < GB_SIZE) {
            return "${"%.2f".format(kbValue / MB_SIZE.toFloat())}${if (bUnit) "Mb/s" else "M"}"
        } else {
            return "${"%.2f".format(kbValue / GB_SIZE.toFloat())}${if (bUnit) "Gb/s" else "G"}"
        }
    }

    private fun formatSecondToText(seconds: Long): String {
        if (seconds < 0) {
            return "xx秒"
        } else if (seconds < MINITE_SIZE) {
            return "${seconds}秒"
        } else if (seconds < HOUR_SIZE) {
            return "${seconds / MINITE_SIZE}分${seconds % MINITE_SIZE}秒"
        } else if (seconds < DAY_SIZE) {
            val tempMinutes = (seconds % HOUR_SIZE) / MINITE_SIZE
            return "${seconds / HOUR_SIZE}小时${tempMinutes}分"
        } else {
            // 大于一天的时间无意义
            return "1天"
        }
    }

    private suspend fun calculateDownLoadSpeed() {
        if (viewModel._apkProgress.value != null) {
            val info = viewModel._apkProgress.value!!
            if (info.path == "") {
                // 计算速率
                val downBytes = info.bytes - viewModel.lastDownLoadValue
                // 计算剩余时间
                val seconds = if (downBytes == 0L) -1 else (info.totalBytes - info.bytes) / downBytes
                // 更新弹框
                updateDialog?.setViewState<TextView>(R.id.tv_desc) {
                    text =
                        Html.fromHtml(
                            "当前速度<font color=\"#DBA366\">${formatByteToText(downBytes, true)}</font>, " +
                                    "预计还需<font color=\"#DBA366\">${formatSecondToText(seconds)}</font>，请稍后～", 1
                        )
                }
                viewModel.lastDownLoadValue = info.bytes
                // 每250毫秒更新一次
                delay(1000)
                calculateDownLoadSpeed()
            } else {
                viewModel.lastDownLoadValue = 0L
            }
        } else {
            delay(1000)
            calculateDownLoadSpeed()
        }
    }
}
