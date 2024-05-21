package com.example.kotlin.storage

import com.example.kotlin.storage.Implementation.AccountServiceImpl
import com.example.kotlin.storage.Implementation.StorageServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAccountService(): AccountService {
        return AccountServiceImpl()
    }

    @Provides
    @Singleton
    fun provideStorageService(accountService: AccountService): StorageService {
        return StorageServiceImpl(accountService)
    }
}