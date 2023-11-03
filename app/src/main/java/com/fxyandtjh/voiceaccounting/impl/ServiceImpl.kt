package com.fxyandtjh.voiceaccounting.impl

import com.fxyandtjh.voiceaccounting.net.NetInterface
import com.fxyandtjh.voiceaccounting.net.RetrofitConfig

class ServiceImpl {
    companion object {
        fun giveINetService(): NetInterface =
            RetrofitConfig.instance.provideRetrofit().create(NetInterface::class.java)
    }
}