package com.fxyandtjh.voiceaccounting.view

import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.fxyandtjh.voiceaccounting.tool.MyPicSelectorStyle
import com.fxyandtjh.voiceaccounting.tool.PicDividerUtil
import com.fxyandtjh.voiceaccounting.tool.PicLoadUtil
import com.fxyandtjh.voiceaccounting.tool.setVisible
import com.fxyandtjh.voiceaccounting.viewmodel.AlbumViewModel
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.style.PictureSelectorStyle
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

    private val moreDialog: RxDialogSet? by lazy {
        context?.let {
            val tempDialog = RxDialogSet(it, R.style.SimpleNoShadowDialog, R.layout.dia_edit_album)
            tempDialog.setViewState<ConstraintLayout>(R.id.container) {
                setLimitClickListener {
                    tempDialog.dismiss()
                }
            }
            tempDialog.setViewState<TextView>(R.id.tv_edit) {
                setLimitClickListener {
                    // 进行编辑相册
                    navController.navigate(AlbumFragmentDirections.actionAlbumFragmentToNewAlbumFragment())
                    tempDialog.dismiss()
                }
            }
            tempDialog.setViewState<TextView>(R.id.tv_delete) {
                setLimitClickListener {
                    // 弹出删除相册的编辑框
                    deleteAlbumDialog?.show()
                    tempDialog.dismiss()
                }
            }
            tempDialog
        }
    }

    private val deleteAlbumDialog: RxDialogSet? by lazy {
        context?.let {
            val tempDialog = RxDialogSet(it, R.style.SimpleDialog, R.layout.dia_delete_confirm)
            tempDialog.setViewState<TextView>(R.id.tv_title) {
                text = getText(R.string.confirm_delete_album)
            }
            tempDialog.setViewState<TextView>(R.id.tv_tips) {
                text = getText(R.string.delete_album_tips)
            }
            tempDialog.setViewState<TextView>(R.id.tv_confirm) {
                setLimitClickListener {
                    // 进行删除相册操作
                    viewModel.deleteAlbum()
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
    private lateinit var footView: View

    private val mAdapter: PicAdapter by lazy {
        // 添加封面Header 与底部边距
        headerView = LayoutInflater.from(context).inflate(R.layout.head_pic, binding.rvPics, false)
        footView =
            LayoutInflater.from(context).inflate(R.layout.default_foot_view, binding.rvPics, false)
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
        tempAdapter.addFooterView(footView)
        tempAdapter
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
            PictureSelector.create(this@AlbumFragment)
                .openGallery(SelectMimeType.ofImage())
                .setImageEngine(GlideEngine.createGlideEngine())
                .setSelectorUIStyle(PictureSelectorStyle())
                .forResult(object : OnResultCallbackListener<LocalMedia?> {
                    override fun onResult(result: ArrayList<LocalMedia?>) {
                        // 当图片获取到后，并发上传到服务器获取到对应的URL
                        handleUriToBase64(result)
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

        binding.ivEdit.setLimitClickListener {
            // 弹出更多弹框
            moreDialog?.show()
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

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel._goBack.collect {
                specialBack?.invoke()
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

    private fun handleUriToBase64(photos: List<LocalMedia?>) {
        val picFiles: List<PicFile> = photos.map { item ->
            val file = File(item?.realPath ?: "")
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
