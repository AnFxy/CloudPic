package com.fxyandtjh.voiceaccounting.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.fxyandtjh.voiceaccounting.base.BaseFragment
import com.fxyandtjh.voiceaccounting.databinding.FragWebviewBinding
import com.fxyandtjh.voiceaccounting.viewmodel.WebViewViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WebViewFragment : BaseFragment<WebViewViewModel, FragWebviewBinding>() {
    private val viewModel: WebViewViewModel by viewModels()

    private val args: WebViewFragmentArgs by navArgs()


    override fun getViewMode(): WebViewViewModel = viewModel

    override fun getViewBinding(inflater: LayoutInflater, parent: ViewGroup?): FragWebviewBinding =
        FragWebviewBinding.inflate(inflater, parent, false)

    override fun setLayout() {
        binding.tvTitle.text = args.webviewInfo.title
        binding.wvWeb.loadUrl(args.webviewInfo.link)
    }
}