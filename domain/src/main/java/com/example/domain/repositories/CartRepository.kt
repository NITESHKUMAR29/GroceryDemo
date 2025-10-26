package com.example.domain.repositories

import com.example.domain.models.CartItem
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun getCartItem(productId: Int): Flow<CartItem?>
    fun getAllCartItems(): Flow<List<CartItem>>
    suspend fun updateCartItem(item: CartItem)
    suspend fun removeItem(productId: Int)
    suspend fun clearCart()
}