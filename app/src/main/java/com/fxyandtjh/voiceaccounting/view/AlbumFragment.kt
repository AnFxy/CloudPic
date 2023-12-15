package com.fxyandtjh.voiceaccounting.view

import android.graphics.Color
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.Utils
import com.fxyandtjh.voiceaccounting.R
import com.fxyandtjh.voiceaccounting.adapter.AlbumAdapter
import com.fxyandtjh.voiceaccounting.adapter.PicAdapter
import com.fxyandtjh.voiceaccounting.base.BaseFragment
import com.fxyandtjh.voiceaccounting.base.Constants
import com.fxyandtjh.voiceaccounting.base.FragDestroyCallBack
import com.fxyandtjh.voiceaccounting.base.setLimitClickListener
import com.fxyandtjh.voiceaccounting.databinding.FragAlbumBinding
import com.fxyandtjh.voiceaccounting.entity.PicFile
import com.fxyandtjh.voiceaccounting.entity.Type
import com.fxyandtjh.voiceaccounting.tool.GlideEngine
import com.fxyandtjh.voiceaccounting.tool.HandlePhoto
import com.fxyandtjh.voiceaccounting.tool.PicDividerUtil
import com.fxyandtjh.voiceaccounting.tool.PicLoadUtil
import com.fxyandtjh.voiceaccounting.tool.dip2px
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
import java.util.ArrayList
import java.util.Locale

@AndroidEntryPoint
class AlbumFragment : BaseFragment<AlbumViewModel, FragAlbumBinding>() {

    private val viewModel: AlbumViewModel by viewModels()

    private val mAdapter: PicAdapter by lazy {
        PicAdapter { picInfo ->
            // 点击后显示大图

        }
    }

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
        val headView = LayoutInflater.from(context).inflate(R.layout.head_pic, null, false)
        val faceView = headView.findViewById<ImageView>(R.id.iv_face)
        val textView = headView.findViewById<TextView>(R.id.tv_count_pic)
        textView.text = "${viewModel._albumInfo.value.total}张照片"
        PicLoadUtil.instance.loadPic(
            context = context,
            url = viewModel._albumInfo.value.faceUrl,
            targetView = faceView
        )

        context?.let {
            binding.rvPics.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = mAdapter
            }
            mAdapter.addHeaderView(headView)
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

        binding.rvPics.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    binding.tvTitle.setTextColor(Utils.getApp().getColor(R.color.black))
                    binding.mToolbar.setBackgroundResource(R.drawable.bg_tran_blue)
                    binding.mToolbar.setNavigationIcon(R.mipmap.back)
                } else {
                    binding.tvTitle.setTextColor(Utils.getApp().getColor(R.color.white))
                    binding.mToolbar.setBackgroundResource(R.drawable.bg_transparent)
                    binding.mToolbar.setNavigationIcon(R.mipmap.back_white)
                }
            }
        })

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
    }

    private fun updateUpBtnStyle(labelId: Int) {
        val btnResourceID = when (labelId) {
            Type.MULTI.weight -> R.mipmap.add_multi
            Type.BABY.weight -> R.mipmap.add_baby
            Type.TRAVEL.weight -> R.mipmap.add_travel
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
}