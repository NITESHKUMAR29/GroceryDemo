package com.example.data.di


import com.example.data.apis.ProductApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideNewsRetrofit(): Retrofit =
        Retrofit.Builder().baseUrl(" https://api.escuelajs.co").addConverterFactory(
            GsonConverterFactory.create()
        ).build()

    @Provides
    @Singleton
    fun provideNewsApiService(retrofit: Retrofit): ProductApiService =
        retrofit.create(ProductApiService::class.java)


}