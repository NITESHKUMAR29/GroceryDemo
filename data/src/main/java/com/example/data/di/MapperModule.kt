package com.example.data.di


import com.example.data.mappers.CartMapper
import com.example.data.mappers.ProductCategoryMapper
import com.example.data.mappers.ProductMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object MapperModule {

    @Provides
    @Singleton
    fun provideProductMapper(): ProductMapper = ProductMapper()

    @Provides
    @Singleton
    fun provideCartMapper(): CartMapper = CartMapper()

    @Provides
    @Singleton
    fun provideProductCategoryMapper(): ProductCategoryMapper = ProductCategoryMapper()


}