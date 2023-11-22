package com.fxyandtjh.voiceaccounting.view

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.PhoneUtils
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
                        if(it.downloadLink != null) {
                            context?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.downloadLink)))
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
        // 进度条
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel._goHomePage.collect {
                binding.ivProgress.setVisible(true)
                for (i in 2 downTo 0) {
                    binding.ivProgress.text = "${i}s"
                    if (i != 0) {
                        delay(1000)
                    } else if (LocalCache.isLogged) {
                        // 登录了，需要进入到首页
                        navController.navigate(SplashFragmentDirections.actionSplashFragmentToMainNavigation())
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
}