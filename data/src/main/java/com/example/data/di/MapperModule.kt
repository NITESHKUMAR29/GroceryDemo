package com.example.data.di


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
    fun provideNewsMapper(): ProductMapper = ProductMapper()

//    @Singleton
//    @Provides
//    fun provideNewsLocalMapper(): NewsLocalMapper = NewsLocalMapper()

}