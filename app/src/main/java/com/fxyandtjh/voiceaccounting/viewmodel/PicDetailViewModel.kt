package com.fxyandtjh.voiceaccounting.viewmodel

import com.fxyandtjh.voiceaccounting.base.BaseViewModel
import com.fxyandtjh.voiceaccounting.entity.PicsDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PicDetailViewModel @Inject constructor() : BaseViewModel() {

    var picData: PicsDetail = PicsDetail(
        picList = emptyList(),
        selectedIndex = -1
    )
}