package com.fxyandtjh.voiceaccounting.inject

import com.fxyandtjh.voiceaccounting.repository.ILoginRepository
import com.fxyandtjh.voiceaccounting.repository.impl.LoginRepository
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
}
