package com.fxyandtjh.voiceaccounting.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.ToastUtils
import com.fxyandtjh.voiceaccounting.R
import com.fxyandtjh.voiceaccounting.base.RxDialogSet
import com.fxyandtjh.voiceaccounting.databinding.ActivityMainBinding
import com.fxyandtjh.voiceaccounting.tool.SecurityUtil
import com.fxyandtjh.voiceaccounting.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    // 网络加载圈
    private val requestDataLoadDialog: RxDialogSet by lazy {
        RxDialogSet.provideDialog(this, R.layout.dia_loading)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // 用户点了授权
            } else {
                // 用户拒绝授权
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        // 设置沉浸式状态栏
        val window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT

        lifecycleScope.launch {
            viewModel._loading.collect {
                if (it) {
                    requestDataLoadDialog.show()
                } else {
                    requestDataLoadDialog.dismiss()
                }
            }
        }
    }

    private fun checkPermissionWithOperate(callback: () -> Unit) {
        // 检查是否授予了存储权限
        if (checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            callback.invoke()
        } else {
            requestPermissionLauncher.launch(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    private fun uploadFileToBaiDuCloud(base64Str: String, fileLength: Int) {
        // 防止被抓包
        if (SecurityUtil.isWifiProxy()) {
            ToastUtils.showShort("你开了WIFI代理，客户端不允许抓包，请关闭!")
            return
        }
        viewModel.obtainTextFormVoice(base64Str, fileLength)
    }
}