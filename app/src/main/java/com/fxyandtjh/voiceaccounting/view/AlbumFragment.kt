package com.fxyandtjh.voiceaccounting.view

import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.Utils
import com.fxyandtjh.voiceaccounting.R
import com.fxyandtjh.voiceaccounting.adapter.PicAdapter
import com.fxyandtjh.voiceaccounting.base.BaseFragment
import com.fxyandtjh.voiceaccounting.base.Constants
import com.fxyandtjh.voiceaccounting.base.FragDestroyCallBack
import com.fxyandtjh.voiceaccounting.base.RxDialogSet
import com.fxyandtjh.voiceaccounting.base.setLimitClickListener
import com.fxyandtjh.voiceaccounting.databinding.FragAlbumBinding
import com.fxyandtjh.voiceaccounting.entity.ExtraPictureInfo
import com.fxyandtjh.voiceaccounting.entity.PicFile
import com.fxyandtjh.voiceaccounting.entity.PicsDetail
import com.fxyandtjh.voiceaccounting.entity.Type
import com.fxyandtjh.voiceaccounting.tool.GlideEngine
import com.fxyandtjh.voiceaccounting.tool.HandlePhoto
import com.fxyandtjh.voiceaccounting.tool.PicDividerUtil
import com.fxyandtjh.voiceaccounting.tool.PicLoadUtil
import com.fxyandtjh.voiceaccounting.tool.SimplePermissionCallBack
import com.fxyandtjh.voiceaccounting.tool.setVisible
import com.fxyandtjh.voiceaccounting.tool.setVisibleWithUnVisual
import com.fxyandtjh.voiceaccounting.viewmodel.AlbumViewModel
import com.huantansheng.easyphotos.Builder.AlbumBuilder
import com.huantansheng.easyphotos.EasyPhotos
import com.huantansheng.easyphotos.callback.SelectCallback
import com.huantansheng.easyphotos.models.album.entity.Photo
import com.huantansheng.easyphotos.setting.Setting
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.util.Locale

@AndroidEntryPoint
class AlbumFragment : BaseFragment<AlbumViewModel, FragAlbumBinding>() {

    private val viewModel: AlbumViewModel by viewModels()

    private val deleteDialog: RxDialogSet? by lazy {
        context?.let {
            val tempDialog = RxDialogSet(it, R.style.SimpleDialog, R.layout.dia_delete_confirm)
            tempDialog.setViewState<TextView>(R.id.tv_confirm) {
                setLimitClickListener {
                    // 进行删除操作
                    viewModel.deleteSelectedPics()
                    tempDialog.dismiss()
                }
            }
            tempDialog.setViewState<TextView>(R.id.tv_cancel) {
                setLimitClickListener {
                    tempDialog.dismiss()
                }
            }
            tempDialog
        }
    }

    private lateinit var headerView: View

    private val mAdapter: PicAdapter by lazy {
        // 添加封面Header
        headerView = LayoutInflater.from(context).inflate(R.layout.head_pic, null, false)
        val faceView = headerView.findViewById<ImageView>(R.id.iv_face)
        val textView = headerView.findViewById<TextView>(R.id.tv_count_pic)
        textView.text = "${viewModel._albumInfo.value.total}张照片"
        // 加载封面
        PicLoadUtil.instance.loadPic(
            context = context,
            url = viewModel._albumInfo.value.faceUrl,
            targetView = faceView
        )
        val tempAdapter = PicAdapter({ selectItem ->
            // 点击后显示大图
            if (viewModel._selectMode.value) {
                return@PicAdapter
            }
            navController.navigate(
                AlbumFragmentDirections.actionAlbumFragmentToPicDetailFragment(
                    PicsDetail(
                        selectedIndex = viewModel._picList.value.indexOf(selectItem),
                        picList = viewModel._picList.value
                    )
                )
            )
        }, { item, isChecked ->
            // 更新选中状态
            viewModel.updateCheckMode(item, isChecked)
        })
        tempAdapter.addHeaderView(headerView)
        tempAdapter
    }

    // 图片选择器
    private val albumBuilder: AlbumBuilder by lazy {
        EasyPhotos.createAlbum(
            this,
            true,
            false,
            GlideEngine.instance
        )
            .setFileProviderAuthority("com.fxyandtjh.voiceaccounting.fileprovider")
            .setCount(10)
            .setVideo(false)
            .setCameraLocation(Setting.LIST_FIRST)
    }

    override fun getViewMode(): AlbumViewModel = viewModel

    override fun getViewBinding(inflater: LayoutInflater, parent: ViewGroup?): FragAlbumBinding =
        FragAlbumBinding.inflate(inflater, parent, false)

    override fun setLayout() {
        context?.let {
            binding.rvPics.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = mAdapter
            }
        }

        specialBack = {
            FragDestroyCallBack(
                key = Constants.NEW_ALBUM,
                data = true
            )
        }
    }

    override fun setObserver() {
        binding.ivUpload.setLimitClickListener {
            albumBuilder.start(object : SelectCallback() {
                override fun onResult(photos: ArrayList<Photo>?, isOriginal: Boolean) {
                    // 当图片获取到后，并发上传到服务器获取到对应的URL
                    photos?.let {
                        handleUriToBase64(it)
                    } ?: ToastUtils.showShort(getText(R.string.no_select_pic))
                }

                override fun onCancel() {
                    ToastUtils.showShort(getText(R.string.no_select_pic))
                }
            })
        }

        // 滑动相册进行样式预览修改
        binding.rvPics.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    binding.tvTitle.setTextColor(Utils.getApp().getColor(R.color.black))
                    binding.tvAll.setTextColor(Utils.getApp().getColor(R.color.black))
                    binding.tvCancel.setTextColor(Utils.getApp().getColor(R.color.black))
                    binding.mToolbar.setBackgroundResource(R.drawable.bg_white)
                    binding.mToolbar.setNavigationIcon(R.mipmap.back)
                    binding.ivEdit.setImageResource(R.mipmap.more)
                    binding.ivAll.setImageResource(R.mipmap.select_all)
                } else {
                    binding.tvTitle.setTextColor(Utils.getApp().getColor(R.color.white))
                    binding.tvAll.setTextColor(Utils.getApp().getColor(R.color.white))
                    binding.tvCancel.setTextColor(Utils.getApp().getColor(R.color.white))
                    binding.mToolbar.setBackgroundResource(R.drawable.bg_tran_black)
                    binding.mToolbar.setNavigationIcon(R.mipmap.back_white)
                    binding.ivEdit.setImageResource(R.mipmap.more_white)
                    binding.ivAll.setImageResource(R.mipmap.select_all_white)
                }
            }
        })

        binding.ivEdit.setLimitClickListener {
            // 显示菜单选项包括 编辑相册
        }

        binding.ivAll.setLimitClickListener {
            // 开启图片编辑模式
            viewModel.updateSelectMode(true)
        }

        binding.tvCancel.setLimitClickListener {
            // 取消图片编辑模式
            viewModel.updateSelectMode(false)
        }

        binding.tvAll.setLimitClickListener {
            // 点击图片全选
            viewModel.updateCheckAllMode()
        }

        binding.btnDelete.setLimitClickListener {
            // 点击删除
            deleteDialog?.show()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel._albumInfo.collect {
                binding.tvTitle.text = it.title

                updateUpBtnStyle(it.labelId)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel._picList.collect {
                val targetData = PicDividerUtil.instance.dividerPicData(it)
                mAdapter.setNewInstance(targetData.toMutableList())
                updateTopTitle(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel._uploadSuccess.collect {
                if (it.first == it.second) {
                    ToastUtils.showShort(getText(R.string.all_upload_success))
                }
                binding.tvTitle.text = "已上传（${it.first}/${it.second}）..."
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel._selectMode.collect { isSelected ->
                updateSelectMode(isSelected)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel._deleteSuccess.collect {
                ToastUtils.showShort(getText(R.string.delete_success))
                viewModel.updateSelectMode(false)
                viewModel.getAlbumFromRemote()
            }
        }
    }

    override fun onRefresh() {
        viewModel.getAlbumFromRemote()
    }

    private fun updateUpBtnStyle(labelId: Int) {
        val btnResourceID = when (labelId) {
            Type.MULTI.weight -> R.mipmap.add_multi
            Type.BABY.weight -> R.mipmap.add_baby
            Type.TRAVEL.weight -> R.mipmap.add_multi
            Type.LOVERS.weight -> R.mipmap.add_lovers
            else -> R.mipmap.add_normal
        }

        PicLoadUtil.instance.loadPic(
            context = context,
            url = btnResourceID,
            targetView = binding.ivUpload
        )
    }

    private fun handleUriToBase64(photos: List<Photo>) {
        val picFiles: List<PicFile> = photos.map { item ->
            val file = File(HandlePhoto.handleImageOkKitKat(item.uri, context))
            val fileInputStream = FileInputStream(file)
            val bytes = ByteArray(fileInputStream.available())
            fileInputStream.read(bytes)
            fileInputStream.close()
            val base64Str = String(Base64.encode(bytes, Base64.NO_WRAP))
            val type = file.extension.uppercase(Locale.getDefault())
            PicFile(base64Str, type)
        }
        viewModel.uploadPictures(picFiles)
    }

    private fun updateTopBarStatus(showAll: Boolean) {
        binding.tvAll.setVisible(showAll)
        binding.tvCancel.setVisible(showAll)
        binding.editContainer.setVisible(showAll)
        binding.ivAll.setVisible(!showAll)
        binding.ivEdit.setVisible(!showAll)
        binding.ivUpload.setVisible(!showAll)
        binding.rvPics.setBackgroundResource(if (showAll) R.drawable.bg_shadow else R.drawable.bg_transparent)
    }

    private fun updateTopTitle(items: List<ExtraPictureInfo>) {
        if (viewModel._selectMode.value) {
            val selectedCount = items.count { it.selected }
            binding.tvTitle.text =
                if (selectedCount == 0)
                    getText(R.string.select_item)
                else
                    "已选择${selectedCount}张"
        } else {
            binding.tvTitle.text = viewModel._albumInfo.value.title
        }
        updateHeaderView(items.size)
    }

    private fun updateSelectMode(isSelected: Boolean) {
        updateTopBarStatus(isSelected)
    }

    private fun updateHeaderView(count: Int) {
        headerView?.findViewById<TextView>(R.id.tv_count_pic)?.apply {
            text = "${count}张照片"
        }
    }
}
