package com.fxyandtjh.voiceaccounting.entity

enum class Type(val weight: Int) {
    NORMAL(-1),
    MULTI(0),
    BABY(1),
    TRAVEL(2),
    LOVERS(3)
}