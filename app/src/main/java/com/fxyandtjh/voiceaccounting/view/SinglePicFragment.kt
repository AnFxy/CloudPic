package com.fxyandtjh.voiceaccounting.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fxyandtjh.voiceaccounting.base.Constants
import com.fxyandtjh.voiceaccounting.databinding.FragSinglePicBinding
import com.fxyandtjh.voiceaccounting.tool.PicLoadUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SinglePicFragment @Inject constructor() : Fragment() {

    private lateinit var binding: FragSinglePicBinding

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
            PicLoadUtil.instance.loadPic(
                context = context,
                url = getString(Constants.PIC_URL, ""),
                targetView = binding.rvPic
            )
        }
    }
}
