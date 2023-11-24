package com.fxyandtjh.voiceaccounting.base

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.PhoneUtils
import com.fxyandtjh.voiceaccounting.R
import kotlinx.coroutines.launch

abstract class BaseFragment<VM : BaseViewModel, VB : ViewBinding> : Fragment() {

    lateinit var binding: VB

    var isHiddenStatus = false

    var specialBack: (() -> Unit)? = null // 特定页面特殊返回操作

    lateinit var navController: NavController

    // viewModel 获取
    private val mviewModel: VM by lazy {
        getViewMode()
    }

    // 网络加载圈
    private val requestDataLoadDialog: RxDialogSet? by lazy {
        context?.let {
            RxDialogSet.provideDialog(it, R.layout.dia_loading)
        }
    }

    // 客服弹框
    private val cusDialog: RxDialogSet? by lazy {
        context?.let {
            val dialog = RxDialogSet.provideDialog(it, R.layout.dia_cus)
            dialog.setViewState<LinearLayoutCompat>(R.id.container_qq) {
                setLimitClickListener {
                    // 调用QQ
                    goQQPage()
                    dialog.dismiss()
                }
            }.setViewState<LinearLayoutCompat>(R.id.container_tel) {
                setLimitClickListener {
                    // 调用系统电话
                    PhoneUtils.dial(Constants.CUS_PHONE)
                    dialog.dismiss()
                }
            }.setViewState<TextView>(R.id.tv_i_know) {
                setLimitClickListener {
                    dialog.dismiss()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = getViewBinding(inflater, container)
        //增加状态栏高度
        context?.run {
            binding.root.findViewById<Toolbar>(R.id.m_toolbar)
                ?.apply {
                    val resourceId: Int =
                        resources.getIdentifier("status_bar_height", "dimen", "android")
                    if (!isHiddenStatus) {
                        setPadding(0, this.resources.getDimensionPixelSize(resourceId), 0, 0)
                    }

                    setLimitMenuClickListener({
                        specialBack?.invoke() ?: activity?.onBackPressed()
                    }, {
                        cusDialog?.show()
                    })
                }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        viewLifecycleOwner.lifecycleScope.launch {
            mviewModel._loading.collect {
                if (it) {
                    requestDataLoadDialog?.show()
                } else {
                    requestDataLoadDialog?.dismiss()
                }
            }
        }

        setLayout()
        setObserver()
    }

    private fun goQQPage() {
        // 打开个人介绍界面（对QQ号）
        val url =
            "mqqapi://card/show_pslcard?src_type=internal&version=1&uin=${Constants.CUS_QQ_NO}&card_type=person&source=qrcode"
        context?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }


    abstract fun getViewMode(): VM

    abstract fun getViewBinding(inflater: LayoutInflater, parent: ViewGroup?): VB

    open fun setLayout() {}

    open fun setObserver() {}
}