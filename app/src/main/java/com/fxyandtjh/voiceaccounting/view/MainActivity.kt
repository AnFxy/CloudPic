package com.fxyandtjh.voiceaccounting.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.WindowInsetsController
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.blankj.utilcode.util.ToastUtils
import com.fxyandtjh.voiceaccounting.BuildConfig
import com.fxyandtjh.voiceaccounting.R
import com.fxyandtjh.voiceaccounting.base.RxDialogSet
import com.fxyandtjh.voiceaccounting.databinding.ActivityMainBinding
import com.fxyandtjh.voiceaccounting.local.LocalCache
import com.fxyandtjh.voiceaccounting.qqsdk.QQBaseUiListener
import com.fxyandtjh.voiceaccounting.tool.SecurityUtil
import com.fxyandtjh.voiceaccounting.tool.setVisible
import com.fxyandtjh.voiceaccounting.viewmodel.MainViewModel
import com.tencent.connect.common.Constants
import com.fxyandtjh.voiceaccounting.base.Constants as MConstants
import com.tencent.tauth.Tencent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {
    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    private lateinit var navController: NavController

    private lateinit var mTencent: Tencent

    private val qqLoginResultListener: QQBaseUiListener = QQBaseUiListener { qqLoginInfo ->
        // QQ 登录成功后，上传 QQ openID， QQ AccessToken， QQ AccessToken过期时间

    }

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
        // Tencent实例
        // 参数为 APP_ID QQ互联中创建的应用ID、全局context、AndroidManifest.xml中FileProvider的authorities属性值
        mTencent = Tencent.createInstance(
            BuildConfig.QQ_APP_ID,
            applicationContext,
            "com.whalecloud.fileprovider"
        )
        initView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        // 设置状态栏图标 为黑色
        window?.decorView?.windowInsetsController?.setSystemBarsAppearance(
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
        )

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as
                NavHostFragment
        navController = navHostFragment.navController

        binding.nvBottom.setupWithNavController(navController)
        navController.addOnDestinationChangedListener(this)

        lifecycleScope.launch {
            viewModel._loading.collect {
                if (it) {
                    requestDataLoadDialog.show()
                } else {
                    requestDataLoadDialog.dismiss()
                }
            }
        }

        binding.nvBottom.setOnItemSelectedListener {
            val builder =
                NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .setRestoreState(true)
                    .setPopUpTo(
                        R.id.homeFragment,
                        inclusive = false,
                        saveState = true
                    )
            navController.navigate(it.itemId, null, builder.build())
            navController.currentDestination?.id == it.itemId
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Tencent.onActivityResultData(requestCode, resultCode, data, qqLoginResultListener)
        if (resultCode == Constants.REQUEST_API) {
            if (resultCode == Constants.REQUEST_LOGIN) {
                Tencent.handleResultData(data, qqLoginResultListener)
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

    fun doQQLogin(): Int {
        if (!mTencent.isSessionValid) {
            if (LocalCache.qqToken.isNotEmpty() && LocalCache.qqOpenId.isNotEmpty()) {
                mTencent.openId = LocalCache.qqOpenId
                mTencent.setAccessToken(LocalCache.qqToken, "${LocalCache.qqTokenExpireTime}")
            }
            return mTencent.login(this, "all", qqLoginResultListener)
        }
        return -1
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        val curLabelName = destination.label

        binding.nvBottom.setVisible(
            curLabelName in listOf("HomeFragment", "NotesFragment", "MyFragment")
        )
    }
}
