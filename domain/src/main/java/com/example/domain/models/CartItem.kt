package com.example.domain.models

data class CartItem(
    val productId: Int,
    val title: String,
    val price: Int,
    val image: String?,
    val quantity: Int
)