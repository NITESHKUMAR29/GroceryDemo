package com.example.data.di

import android.content.Context
import androidx.room.Room
import com.example.data.local.CartDatabase
import com.example.data.local.dao.CartDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): CartDatabase {
        return Room.databaseBuilder(
            context,
            CartDatabase::class.java,
            "news_db"
        ).build()
    }

    @Provides
    fun provideNewsDao(db: CartDatabase): CartDao = db.cartDao()
}