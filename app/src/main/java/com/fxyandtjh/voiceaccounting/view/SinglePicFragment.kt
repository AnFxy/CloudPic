package com.fxyandtjh.voiceaccounting.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fxyandtjh.voiceaccounting.base.Constants
import com.fxyandtjh.voiceaccounting.base.setLimitClickListener
import com.fxyandtjh.voiceaccounting.databinding.FragSinglePicBinding
import com.github.panpf.sketch.displayImage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SinglePicFragment @Inject constructor() : Fragment() {

    private lateinit var binding: FragSinglePicBinding

    var clickViewCallBack: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragSinglePicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.takeIf { it.containsKey(Constants.PIC_URL) }?.apply {
            binding.rvPic.displayImage(getString(Constants.PIC_URL, "")) {
                crossfade()
            }
        }

        binding.rvPic.setLimitClickListener {
            clickViewCallBack?.invoke()
        }
    }

    public fun rotateImage() {
        binding.rvPic.rotateBy(90)
    }
}
