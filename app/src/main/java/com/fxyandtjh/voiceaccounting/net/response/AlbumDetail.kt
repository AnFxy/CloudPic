package com.fxyandtjh.voiceaccounting.net.response

data class AlbumDetail(
    val faceUrl: String,
    val albumId: String,
    val total: Int,
    val title: String,
    val labelId: Int,
    val createTime: Long,
    val id: String,
    val picList: List<PictureInfo>,
    val subscribers: List<Subscriber>
)
