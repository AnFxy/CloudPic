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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.UriUtils
import com.fxyandtjh.voiceaccounting.R
import com.fxyandtjh.voiceaccounting.base.BaseFragment
import com.fxyandtjh.voiceaccounting.base.Constants
import com.fxyandtjh.voiceaccounting.base.FragDestroyCallBack
import com.fxyandtjh.voiceaccounting.base.RxDialogSet
import com.fxyandtjh.voiceaccounting.base.setLimitClickListener
import com.fxyandtjh.voiceaccounting.databinding.FragNewAblumBinding
import com.fxyandtjh.voiceaccounting.entity.Type
import com.fxyandtjh.voiceaccounting.tool.HandlePhoto
import com.fxyandtjh.voiceaccounting.tool.SimplePermissionCallBack
import com.fxyandtjh.voiceaccounting.tool.setVisible
import com.fxyandtjh.voiceaccounting.viewmodel.NewAlbumViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.util.Locale

@AndroidEntryPoint
class NewAlbumFragment : BaseFragment<NewAlbumViewModel, FragNewAblumBinding>() {
    private val viewModel: NewAlbumViewModel by viewModels()

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

    override fun getViewMode(): NewAlbumViewModel = viewModel

    override fun getViewBinding(inflater: LayoutInflater, parent: ViewGroup?): FragNewAblumBinding =
        FragNewAblumBinding.inflate(inflater, parent, false)

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

    }

    override fun setLayout() {
        binding.themeNormal.callBackClick = {
            viewModel.updateSelectedIndex(Type.NORMAL)
        }
        binding.themeMulti.callBackClick = {
            viewModel.updateSelectedIndex(Type.MULTI)
        }
        binding.themeBaby.callBackClick = {
            viewModel.updateSelectedIndex(Type.BABY)
        }
        binding.themeTravel.callBackClick = {
            viewModel.updateSelectedIndex(Type.TRAVEL)
        }
        binding.themeLovers.callBackClick = {
            viewModel.updateSelectedIndex(Type.LOVERS)
        }
    }

    override fun setObserver() {
        // 修改相册封面
        binding.livFace.callBackClickHead = {
            // 当修改相册时，要判断相册名是否已经修改了
            albumDialog?.show()
        }

        binding.livName.callBackTextChanged = {
            binding.btnSubmit.isEnabled = it != ""
        }

        // 创建相册
        binding.btnSubmit.setLimitClickListener {
            val currentTitle = binding.livName.valueT
            viewModel.updateTitle(currentTitle)
            viewModel.createAlbum()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel._pageData.collect { pageData ->
                val currentType = pageData.currentSelectTag
                binding.themeNormal.isChecked = currentType == Type.NORMAL
                binding.themeMulti.isChecked = currentType == Type.MULTI
                binding.themeBaby.isChecked = currentType == Type.BABY
                binding.themeTravel.isChecked = currentType == Type.TRAVEL
                binding.themeLovers.isChecked = currentType == Type.LOVERS
                updateThemeDes(currentType)

                binding.livFace.valueT = pageData.faceUrl
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel._createAlbumSuccess.collect {
                FragDestroyCallBack(
                    key = Constants.NEW_ALBUM,
                    data = true
                )
            }
        }
    }

    private fun updateThemeDes(currentType: Type) {
        binding.tvDes.setVisible(currentType != Type.NORMAL)
        when (currentType) {
            Type.MULTI -> {
                binding.tvDes.text = getText(R.string.multi_people_des)
                binding.livTheme.valueT = "${getText(R.string.multi_people)}相册"
            }
            Type.BABY -> {
                binding.tvDes.text = getText(R.string.baby_des)
                binding.livTheme.valueT = "${getText(R.string.baby)}相册"
            }
            Type.TRAVEL -> {
                binding.tvDes.text = getText(R.string.travel_des)
                binding.livTheme.valueT = "${getText(R.string.travel)}相册"
            }
            Type.LOVERS -> {
                binding.tvDes.text = getText(R.string.lovers_des)
                binding.livTheme.valueT = "${getText(R.string.lovers)}相册"
            }
            else -> {
                binding.tvDes.text = ""
                binding.livTheme.valueT = "${getText(R.string.normal)}相册"
            }
        }
    }
}