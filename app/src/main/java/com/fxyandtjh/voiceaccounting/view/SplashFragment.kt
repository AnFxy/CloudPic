package com.fxyandtjh.voiceaccounting.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.fxyandtjh.voiceaccounting.BuildConfig
import com.fxyandtjh.voiceaccounting.base.BaseFragment
import com.fxyandtjh.voiceaccounting.base.setLimitClickListener
import com.fxyandtjh.voiceaccounting.databinding.FragSplashBinding
import com.fxyandtjh.voiceaccounting.local.LocalCache
import com.fxyandtjh.voiceaccounting.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : BaseFragment<SplashViewModel, FragSplashBinding>() {
    private val viewModel: SplashViewModel by viewModels()

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
        // 进度条
        lifecycleScope.launch {
            for (i in 3 downTo 0) {
                binding.ivProgress.text = "${i}s"
                if (i != 0) {
                    delay(1000)
                } else if (LocalCache.isLogged) {
                    // 登录了，需要进入到首页
                } else {
                    // 进入到登录页面
                    navController.navigate(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
                }
            }
        }

        binding.ivProgress.setLimitClickListener {
            navController.navigate(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
        }
    }
}