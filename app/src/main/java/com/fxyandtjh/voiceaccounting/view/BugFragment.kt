package com.fxyandtjh.voiceaccounting.view

import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.fxyandtjh.voiceaccounting.R
import com.fxyandtjh.voiceaccounting.adapter.PicBugAdapter
import com.fxyandtjh.voiceaccounting.base.BaseFragment
import com.fxyandtjh.voiceaccounting.base.setLimitClickListener
import com.fxyandtjh.voiceaccounting.databinding.FragBugBinding
import com.fxyandtjh.voiceaccounting.tool.HandlePhoto
import com.fxyandtjh.voiceaccounting.viewmodel.BugViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.util.Locale

@AndroidEntryPoint
class BugFragment : BaseFragment<BugViewModel, FragBugBinding>() {
    private val viewModel: BugViewModel by viewModels()

    private lateinit var albumLauncher: ActivityResultLauncher<String>

    private val SPAN_COUNT = 3

    private val mAdapter: PicBugAdapter by lazy {
        // 一行放三个
        val targetAdapter = PicBugAdapter(SPAN_COUNT)
        targetAdapter.callBackClickTrashCan = { index ->
            viewModel.currentIndex = index
            updateEditTextValue()
            viewModel.removePicture()
        }
        targetAdapter.callBackClickPic = { index ->
            viewModel.currentIndex = index
            updateEditTextValue()
            albumLauncher.launch("image/*")
        }
        targetAdapter
    }


    override fun getViewMode(): BugViewModel = viewModel

    override fun getViewBinding(inflater: LayoutInflater, parent: ViewGroup?): FragBugBinding =
        FragBugBinding.inflate(inflater, parent, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 相册
        albumLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { url ->
            url?.let {
                try {
                    val file = File(HandlePhoto.handleImageOkKitKat(it, context))
                    val fileInputStream = FileInputStream(file)
                    val bytes = ByteArray(fileInputStream.available())
                    fileInputStream.read(bytes)
                    fileInputStream.close()
                    val base64Str = String(Base64.encode(bytes, Base64.NO_WRAP))
                    val type = file.extension.uppercase(Locale.getDefault())
                    viewModel.uploadPicture(base64Str, type)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun setLayout() {
        binding.rvPic.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = mAdapter
        }
    }

    override fun setObserver() {
        binding.containerCommit.setLimitClickListener {
            // 将输入框数据同步到 viewModel中
            updateEditTextValue()
            viewModel.commitBug()
        }

        binding.typeContainer.setOnCheckedChangeListener { _, checkedId ->
            val currentValue = if (checkedId == R.id.type_bug) 1 else 0
            if (currentValue != viewModel._pageData.value.type) {
                updateEditTextValue()
                viewModel.updatePage(currentValue)
            }
        }

        binding.etDes.addTextChangedListener {
            binding.tvDesCount.text = "${it?.toString()?.length ?: 0}/100"
        }

        binding.tvHistory.setLimitClickListener {
            navController.navigate(BugFragmentDirections.actionBugFragmentToBugHisFragment())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel._pageData.collect { bugInfo ->
                val targetList = bugInfo.images.toMutableList()
                if (targetList.size < SPAN_COUNT) {
                    // 添加空白可上传图片组件
                    targetList.add("")
                }
                mAdapter.setNewInstance(targetList)

                binding.typeBug.isChecked = bugInfo.type == 1
                binding.typeOpt.isChecked = bugInfo.type == 0
                binding.tvDesCount.text = "${bugInfo.content.length}/100"
                binding.tvPicCount.text = "${bugInfo.images.size}/3"
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel._goBackEvent.collect { type ->
                when(type) {
                    TYPE.GO_BACK.value -> {
                        ToastUtils.showShort(getText(R.string.success_commit))
                        resetInput()
                    }
                    TYPE.ERROR_EMAIL.value -> ToastUtils.showShort(getText(R.string.error_email))
                    TYPE.ERROR_CONTENT.value -> ToastUtils.showShort(getText(R.string.error_content))
                }
            }
        }
    }

    private fun updateEditTextValue() {
        // 将输入框数据同步到 viewModel中
        viewModel.updatePage(
            email = binding.etEmail.text.toString().trim(),
            content = binding.etDes.text.toString().trim()
        )
    }

    private fun resetInput() {
        binding.etEmail.setText("")
        binding.etDes.setText("")
    }

    enum class TYPE(val value: Int) {
        GO_BACK(0),
        ERROR_EMAIL(1),
        ERROR_CONTENT(2)
    }
}