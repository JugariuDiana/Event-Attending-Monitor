package com.example.kotlin.account

import com.example.kotlin.account.Implementation.AccountServiceImpl
import com.example.kotlin.account.Implementation.EventStorageServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds
    abstract fun provideAccountService(impl: AccountServiceImpl): AccountService

    @Binds
    abstract fun provideStorageService(impl: EventStorageServiceImpl): EventStorageService

}