package com.fxyandtjh.voiceaccounting.view

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.UriUtils
import com.fxyandtjh.voiceaccounting.R
import com.fxyandtjh.voiceaccounting.base.BaseFragment
import com.fxyandtjh.voiceaccounting.base.Constants
import com.fxyandtjh.voiceaccounting.base.FragDestroyCallBack
import com.fxyandtjh.voiceaccounting.base.RxDialogSet
import com.fxyandtjh.voiceaccounting.base.setLimitClickListener
import com.fxyandtjh.voiceaccounting.base.setLimitMenuClickListener
import com.fxyandtjh.voiceaccounting.databinding.FragUserInfoBinding
import com.fxyandtjh.voiceaccounting.net.response.UserInfo
import com.fxyandtjh.voiceaccounting.tool.HandlePhoto
import com.fxyandtjh.voiceaccounting.tool.SimplePermissionCallBack
import com.fxyandtjh.voiceaccounting.tool.timeStampToDate
import com.fxyandtjh.voiceaccounting.viewmodel.UserInfoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.util.Locale

@AndroidEntryPoint
class UserInfoFragment : BaseFragment<UserInfoViewModel, FragUserInfoBinding>() {
    private val viewModel: UserInfoViewModel by viewModels()

    private lateinit var albumLauncher: ActivityResultLauncher<String>
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>

    private val albumDialog: RxDialogSet? by lazy {
        context?.let {
            val dialog = RxDialogSet.provideDialog(it, R.layout.dia_pic_select)
            dialog.setViewState<TextView>(R.id.tv_camera) {
                setLimitClickListener {
                    // 调用相机
                    PermissionUtils.permission(
                        PermissionConstants.CAMERA,
                        PermissionConstants.STORAGE
                    ).callback(SimplePermissionCallBack {
                        cameraLauncher.launch(HandlePhoto.createImageUri(context))
                        dialog.dismiss()
                    }).request()
                }
            }.setViewState<TextView>(R.id.tv_album) {
                setLimitClickListener {
                    // 调用相册
                    PermissionUtils.permission(
                        PermissionConstants.STORAGE
                    ).callback(SimplePermissionCallBack {
                        albumLauncher.launch("image/*")
                        dialog.dismiss()
                    }).request()
                }
            }.setViewState<ConstraintLayout>(R.id.pic_select_container) {
                setLimitClickListener {
                    dialog.dismiss()
                }
            }
        }
    }

    override fun getViewMode(): UserInfoViewModel = viewModel

    override fun getViewBinding(inflater: LayoutInflater, parent: ViewGroup?): FragUserInfoBinding =
        FragUserInfoBinding.inflate(inflater, parent, false)

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
        // 相机
        cameraLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
                if (isSuccess) {
                    context?.let {
                        val imageType =
                            ImageUtils.getImageType(UriUtils.uri2File(HandlePhoto.mCameraUri!!))
                        val inputStream =
                            it.contentResolver.openInputStream(HandlePhoto.mCameraUri)
                        val bitmap =
                            BitmapFactory.decodeStream(inputStream)
                        viewModel.uploadCameraPic(bitmap, imageType.value)
                    }
                }
            }

        // 返回回调
        specialBack = {
            FragDestroyCallBack(
                key = Constants.USER_FRAG,
                data = true
            )
        }
    }

    override fun setObserver() {
        binding.tvEdit.setLimitClickListener {
            if (viewModel._editAble.value) {
                // 判断个性签名、昵称、性别是否修改
                val currentDes = binding.etDes.text.toString()
                val currentNickName = binding.livName.value
                val currentGender = binding.livGender.value
                viewModel.uploadPageDataToRemote(currentDes, currentNickName, currentGender)
            } else {
                viewModel.updateEditAbleStatus()
            }
        }

        binding.etDes.addTextChangedListener {
            val currentText = it?.toString() ?: ""
            binding.livDes.value = "${currentText.length}/30"
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel._editAble.collect {
                updateViewEnable(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel._pageData.collect {
                updatePageData(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel._updateEvent.collect {
                viewModel.updateEditAbleStatus()
                ToastUtils.showShort(getText(R.string.update_success))
            }
        }

        setViewDataChangedByUser()
    }

    private fun setViewDataChangedByUser() {
        // 当用户修改头像时
        binding.livHead.callBackClickHead = {
            // 当用户点击头像时，弹出头像选择框
            // 判断个性签名、昵称、性别是否修改
            val currentDes = binding.etDes.text.toString()
            val currentNickName = binding.livName.value
            val currentGender = binding.livGender.value
            viewModel.updateEditDataToVM(currentDes,currentNickName, currentGender)
            albumDialog?.show()
        }
    }

    private fun updateViewEnable(isEnable: Boolean) {
        binding.livHead.isEnable = isEnable
        binding.livName.isEnable = isEnable
        binding.livGender.isEnable = isEnable
        binding.livCreateTime.isEnable = isEnable
        binding.livDes.isEnable = isEnable

        // 个签输入
        binding.etDes.isEnabled = isEnable

        // 编辑按钮文本
        binding.tvEdit.text = if (isEnable) getText(R.string.save) else getText(R.string.edit)
    }

    private fun updatePageData(userInfo: UserInfo) {
        binding.livHead.value = userInfo.headUrl
        binding.livName.value = userInfo.name
        binding.livGender.value = userInfo.gender.toString()
        binding.livCreateTime.value = timeStampToDate(userInfo.registerTime)
        binding.etDes.setText(userInfo.des)
    }
}