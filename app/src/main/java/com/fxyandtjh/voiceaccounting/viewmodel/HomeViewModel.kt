package com.fxyandtjh.voiceaccounting.viewmodel

import com.fxyandtjh.voiceaccounting.base.BaseViewModel
import com.fxyandtjh.voiceaccounting.net.response.AlbumInfo
import com.fxyandtjh.voiceaccounting.repository.impl.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : BaseViewModel() {
    private val initList = ArrayList<AlbumInfo>()
    private val albumList: MutableStateFlow<List<AlbumInfo>> = MutableStateFlow(initList)

    var _albumList: StateFlow<List<AlbumInfo>> = albumList

    init {
        getAlbumListFromRemote()
    }

    fun getAlbumListFromRemote() {
        launchUIWithDialog ({
            initList.apply {
                clear()
                add(AlbumInfo(faceUrl = "", title = "", labelId = 0, total = 0, createTime = 0, id = ""))
                addAll(mainRepository.getAlbumListFromRemote())
            }
            albumList.value = initList
        },{
            initList.apply {
                clear()
                add(AlbumInfo(faceUrl = "", title = "", labelId = 0, total = 0, createTime = 0, id = ""))
            }
            albumList.value = initList
        })
    }
}