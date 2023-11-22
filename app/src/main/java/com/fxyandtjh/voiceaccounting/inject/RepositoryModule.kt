package com.fxyandtjh.voiceaccounting.inject

import com.fxyandtjh.voiceaccounting.repository.ILoginRepository
import com.fxyandtjh.voiceaccounting.repository.IMainRepository
import com.fxyandtjh.voiceaccounting.repository.IStartupRepository
import com.fxyandtjh.voiceaccounting.repository.impl.LoginRepository
import com.fxyandtjh.voiceaccounting.repository.impl.MainRepository
import com.fxyandtjh.voiceaccounting.repository.impl.StartupRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun createLoginRepository(
        beginRepository: LoginRepository
    ): ILoginRepository

    @Binds
    abstract fun createMainRepository(
        mainRepository: MainRepository
    ): IMainRepository

    @Binds
    abstract fun createStartupRepository(
        startupRepository: StartupRepository
    ): IStartupRepository
}
