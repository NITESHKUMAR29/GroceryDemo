package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val productId: Int,
    val title: String,
    val price: Int,
    val image: String?,
    val quantity: Int
)