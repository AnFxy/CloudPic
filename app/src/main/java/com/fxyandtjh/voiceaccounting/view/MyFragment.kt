package com.fxyandtjh.voiceaccounting.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.ToastUtils
import com.fxyandtjh.voiceaccounting.BuildConfig
import com.fxyandtjh.voiceaccounting.R
import com.fxyandtjh.voiceaccounting.StartupNavigationDirections
import com.fxyandtjh.voiceaccounting.base.BaseFragment
import com.fxyandtjh.voiceaccounting.base.Constants
import com.fxyandtjh.voiceaccounting.base.RxDialogSet
import com.fxyandtjh.voiceaccounting.base.receiveCallBackDataFromLastFragment
import com.fxyandtjh.voiceaccounting.base.setLimitClickListener
import com.fxyandtjh.voiceaccounting.databinding.FragMyBinding
import com.fxyandtjh.voiceaccounting.net.response.UserInfo
import com.fxyandtjh.voiceaccounting.tool.PicLoadUtil
import com.fxyandtjh.voiceaccounting.tool.encryptionPhoneNumber
import com.fxyandtjh.voiceaccounting.tool.setVisible
import com.fxyandtjh.voiceaccounting.viewmodel.MyViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyFragment : BaseFragment<MyViewModel, FragMyBinding>() {
    private val viewModel: MyViewModel by viewModels()

    // 挽留弹框
    private val logoutDialog: RxDialogSet? by lazy {
        context?.let {
            val dialog = RxDialogSet.provideDialog(it, R.layout.dia_logout)
            dialog.setViewState<TextView>(R.id.tv_cancel) {
                setLimitClickListener {
                    dialog.dismiss()
                }
            }.setViewState<TextView>(R.id.tv_confirm) {
                setLimitClickListener {
                    // 退出登录
                    viewModel.doLogout()
                }
            }
        }
    }

    // 更新弹框
    private val updateDialog: RxDialogSet? by lazy {
        context?.let {
            RxDialogSet.provideDialog(it, R.layout.dia_update)
        }
    }

    override fun getViewMode(): MyViewModel = viewModel

    override fun getViewBinding(inflater: LayoutInflater, parent: ViewGroup?): FragMyBinding =
        FragMyBinding.inflate(inflater, parent, false)

    override fun setLayout() {
        binding.livVersion.value = viewModel.version
        binding.livCache.value = viewModel.cacheValue

        receiveCallBackDataFromLastFragment<Boolean>(key = Constants.USER_FRAG) {
            viewModel.obtainPersonalInformation()
        }
    }

    override fun setObserver() {
        binding.livVersion.setLimitClickListener {
            viewModel.checkUpdate()
        }

        binding.livCache.setLimitClickListener {
            ToastUtils.showShort(getText(R.string.no_cache))
        }

        binding.tvLogout.setLimitClickListener {
            // 点击退出登录 弹出挽留弹框
            logoutDialog?.show()
        }

        binding.livInfo.setLimitClickListener {
            // 点击进入个人信息页面
            navController.navigate(MyFragmentDirections.actionMyFragmentToUserInfoFragment())
        }

        binding.livBug.setLimitClickListener {
            navController.navigate(MyFragmentDirections.actionMyFragmentToBugFragment())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel._goLogin.collect {
                logoutDialog?.dismiss()
                navController.navigate(StartupNavigationDirections.justGoLogin())
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel._userInfoData.collect {
                updateUserInformation(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel._showUpdate.collect {
                if (BuildConfig.VERSION_CODE >= it.versionCode) {
                    // 不需要更新
                    ToastUtils.showShort(getText(R.string.update_newest))
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
                            // 手动检测更新的我们让他可以手动关闭
                            updateDialog?.dismiss()
                        }
                    }
                }?.setViewState<TextView>(R.id.tv_close) {
                    setVisible(BuildConfig.VERSION_CODE >= it.miniVersionCode)
                    setLimitClickListener {
                        // 直接关闭 弹框并 走之后的流程
                        updateDialog?.dismiss()
                    }
                }
                updateDialog?.show()
            }
        }
    }

    private fun updateUserInformation(userInfo: UserInfo) {
        binding.tvName.text = userInfo.name
        binding.tvPhoneNumber.text = encryptionPhoneNumber(userInfo.phoneNumber)
        binding.tvDes.text = if (userInfo.des == "") getText(R.string.default_dec) else userInfo.des

        // 加载头像
        PicLoadUtil.instance.loadPic(
            context = context,
            url = if (userInfo.headUrl != "") userInfo.headUrl else R.mipmap.default_head,
            radius = 25f,
            borderWidth = 5f,
            targetView = binding.ivHead
        )
        // 加载性别Icon
        PicLoadUtil.instance.loadPic(
            context = context,
            url = if (userInfo.gender == 1) R.mipmap.male else R.mipmap.female,
            targetView = binding.ivGender
        )
    }
}
