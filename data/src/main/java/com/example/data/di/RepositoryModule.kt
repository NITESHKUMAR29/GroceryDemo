package com.example.data.di

import com.example.data.repositories.CartRepositoryImpl
import com.example.data.repositories.ProductRepositoryImpl
import com.example.domain.repositories.CartRepository
import com.example.domain.repositories.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindNewsRepository(
        impl: ProductRepositoryImpl
    ): ProductRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(
        impl: CartRepositoryImpl
    ): CartRepository
}
