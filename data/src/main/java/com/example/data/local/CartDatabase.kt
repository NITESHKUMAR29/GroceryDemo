package com.example.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.local.dao.CartDao
import com.example.data.local.entities.CartItemEntity

@Database(entities = [CartItemEntity::class], version = 1, exportSchema = false)
abstract class CartDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
}