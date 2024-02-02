package com.fxyandtjh.voiceaccounting.view

import android.net.http.SslError
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.fxyandtjh.voiceaccounting.base.BaseFragment
import com.fxyandtjh.voiceaccounting.databinding.FragWebviewBinding
import com.fxyandtjh.voiceaccounting.tool.setVisible
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
        binding.wvWeb.apply {
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    binding.pbWebView.apply {
                        progress = newProgress
                        setVisible(newProgress != 100)
                    }
                }
            }
            webViewClient = object : WebViewClient() {
                override fun onReceivedSslError(
                    view: WebView?,
                    handler: SslErrorHandler?,
                    error: SslError?
                ) {
                    handler?.proceed()
                }
            }
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.javaScriptEnabled = true
            settings.setSupportZoom(false)
            settings.builtInZoomControls = true
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
            settings.domStorageEnabled = true
            loadUrl(args.webviewInfo.link)
        }

    }
}