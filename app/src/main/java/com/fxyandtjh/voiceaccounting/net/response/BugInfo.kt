package com.fxyandtjh.voiceaccounting.net.response

data class BugInfo(
    val type: Int, // 类型 优化0|BUG1
    val content: String,
    val images: List<String>,
    val email: String,
    val status: Int, // 审核状态 0 未处理 1 处理中 2 已完成(已修复｜已优化) 3 拒绝
    val createTime: Long
)
