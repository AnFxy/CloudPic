package com.fxyandtjh.voiceaccounting.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.fxyandtjh.voiceaccounting.R
import com.fxyandtjh.voiceaccounting.StartupNavigationDirections
import com.fxyandtjh.voiceaccounting.base.BaseFragment
import com.fxyandtjh.voiceaccounting.base.RxDialogSet
import com.fxyandtjh.voiceaccounting.base.setLimitClickListener
import com.fxyandtjh.voiceaccounting.databinding.FragMyBinding
import com.fxyandtjh.voiceaccounting.net.response.UserInfo
import com.fxyandtjh.voiceaccounting.tool.PicLoadUtil
import com.fxyandtjh.voiceaccounting.tool.encryptionPhoneNumber
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

    override fun getViewMode(): MyViewModel = viewModel

    override fun getViewBinding(inflater: LayoutInflater, parent: ViewGroup?): FragMyBinding =
        FragMyBinding.inflate(inflater, parent, false)

    override fun setObserver() {
        binding.tvLogout.setLimitClickListener {
            // 点击退出登录 弹出挽留弹框
            logoutDialog?.show()
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
    }

    private fun updateUserInformation(userInfo: UserInfo) {
        binding.tvName.text = userInfo.name
        binding.tvPhoneNumber.text = encryptionPhoneNumber(userInfo.phoneNumber)
        // 加载头像
        PicLoadUtil.instance.loadPic(
            context = context,
            url = if (userInfo.headUrl != "") userInfo.headUrl else R.mipmap.default_head,
            radius = 25f,
            borderWidth = 5f,
            targetView = binding.ivHead
        )
    }
}
