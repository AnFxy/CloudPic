package com.fxyandtjh.voiceaccounting.view

import android.graphics.Color
import android.graphics.Typeface
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.fxyandtjh.voiceaccounting.R
import com.fxyandtjh.voiceaccounting.base.BaseFragment
import com.fxyandtjh.voiceaccounting.databinding.FragLoginBinding
import com.fxyandtjh.voiceaccounting.entity.LoginInfo
import com.fxyandtjh.voiceaccounting.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.regex.Matcher
import java.util.regex.Pattern

@AndroidEntryPoint
class LoginFragment : BaseFragment<LoginViewModel, FragLoginBinding>() {
    private val viewModel: LoginViewModel by viewModels()

    private lateinit var animation: Animation

    override fun getViewMode(): LoginViewModel = viewModel

    override fun getViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): FragLoginBinding = FragLoginBinding.inflate(inflater, parent, false)

    override fun setLayout() {
        // 设置 logo 为圆角
        val options = RequestOptions()
            .transform(RoundedCorners(20))
        Glide.with(this).apply {
            applyDefaultRequestOptions(options)
        }.load(R.mipmap.logo).into(binding.imgLogo)

        // 设置动画
        animation = AnimationUtils.loadAnimation(activity, R.anim.bg_anim)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                // 不做处理
            }

            override fun onAnimationEnd(animation: Animation?) {
                // 切换头像
                Glide.with(this@LoginFragment).apply {
                    applyDefaultRequestOptions(options)
                }.load(
                    if (viewModel._selectedPassword.value)
                        R.mipmap.logo_float
                    else
                        R.mipmap.logo
                ).into(binding.imgLogo)
            }

            override fun onAnimationRepeat(animation: Animation?) {
                // 不做处理
            }
        })
    }

    override fun setObserver() {
        binding.btnLogin.setOnClickListener {
            if (binding.btnLogin.isChecked != viewModel._selectedLogin.value) {
                viewModel.changeSelectedState()
            }
        }

        binding.btnForgetPw.setOnClickListener {
            // TODO 前往密保页面
        }

        binding.btnRegister.setOnClickListener {
            if (binding.btnRegister.isChecked == viewModel._selectedLogin.value) {
                viewModel.changeSelectedState()
            }
        }

        binding.editPassword.setOnFocusChangeListener { _, hasFocus ->
            // 启动动画
            viewModel.updateSelectedPassword(hasFocus)
            binding.imgLogo.startAnimation(animation)
        }

        binding.editNumber.addTextChangedListener {
            viewModel.updatePhoneNumber(it?.toString()?.trim() ?: "")
        }

        binding.editPassword.addTextChangedListener {
            viewModel.updatePw(it?.toString()?.trim() ?: "")
        }

        binding.editConfirmPassword.addTextChangedListener {
            viewModel.updateConfirmPw(it?.toString()?.trim() ?: "")
        }

        binding.checkEyes.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateEyes(isChecked)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel._selectedLogin.collect { value ->
                updateButtonsSelected(value)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel._eyesClose.collect { isClose ->
                binding.editPassword.inputType =
                    if (isClose)
                        0x00000081
                    else
                        0x00000001
                binding.editConfirmPassword.inputType =
                    if (isClose)
                        0x00000081
                    else
                        0x00000001
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel._pageData.collect { value ->
                checkAccountAndPW(value)
            }
        }
    }

    private fun updateButtonsSelected(showLogin: Boolean) {
        // 登录按钮状态变更
        binding.btnLogin.textSize = if (showLogin) 22f else 20f
        binding.btnLogin.typeface = if (showLogin) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        binding.btnLogin.setTextColor(
            if (showLogin)
                Color.parseColor("#A7A3C3")
            else
                Color.parseColor("#999999")
        )
        // 注册按钮状态变更
        binding.btnRegister.textSize = if (!showLogin) 22f else 20f
        binding.btnRegister.typeface = if (!showLogin) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        binding.btnRegister.setTextColor(
            if (!showLogin)
                Color.parseColor("#A7A3C3")
            else
                Color.parseColor("#999999")
        )
        // 忘记密码、确认密码 隐藏
        binding.btnForgetPw.visibility = if (!showLogin) View.GONE else View.VISIBLE
        binding.editConfirmPassword.visibility = if (showLogin) View.GONE else View.VISIBLE

        // 标题栏、底部登录/注册按钮文案
        binding.tvTitle.text =
            if (showLogin) getText(R.string.login) else getText(R.string.register)
        binding.btnSubmit.text =
            if (showLogin) getText(R.string.login) else getText(R.string.register)

        // 检测是否可登录/注册
        checkAccountAndPW(viewModel._pageData.value)
    }

    // 校验号码、密码、确认密码的合法性
    private fun checkAccountAndPW(loginInfo: LoginInfo) {
        // 判断手机号码是否是 1开头，且11位数
        val phoneResult = Pattern.compile("^1[0-9]{10}$").matcher(loginInfo.phoneNumber)
        val isPhoneNumOk = phoneResult.find()
        val pwResult = Pattern.compile("^[a-zA-Z0-9]{8,16}$").matcher(loginInfo.password)
        val isPWOk = pwResult.find()
        val isConfirmPWOK = loginInfo.password == loginInfo.confirmPassword
        binding.btnSubmit.isEnabled  = isPhoneNumOk && isPWOk
                && (viewModel._selectedLogin.value || (!viewModel._selectedLogin.value && isConfirmPWOK))
    }
}