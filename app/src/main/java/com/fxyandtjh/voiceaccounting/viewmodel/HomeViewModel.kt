package com.fxyandtjh.voiceaccounting.viewmodel

import com.fxyandtjh.voiceaccounting.base.BaseViewModel
import com.fxyandtjh.voiceaccounting.net.response.AlbumInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(

) : BaseViewModel() {
    private val albumList: MutableStateFlow<List<AlbumInfo>> = MutableStateFlow(emptyList())
    var _albumList: StateFlow<List<AlbumInfo>> = albumList

    fun getAlbumListFromRemote() {
        launchUIWithDialog {

        }
    }
}