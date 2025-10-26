package com.example.grocery.di

import com.example.domain.utility.NetworkChecker
import com.example.grocery.utility.NetworkHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideNetworkChecker(networkHelper: NetworkHelper): NetworkChecker = networkHelper
}