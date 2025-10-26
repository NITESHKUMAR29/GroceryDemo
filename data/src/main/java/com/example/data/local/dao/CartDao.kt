package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entities.CartItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Query("SELECT * FROM cart_items")
    fun getAllCartItems(): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items WHERE productId = :productId LIMIT 1")
    fun getCartItem(productId: Int): Flow<CartItemEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE productId = :productId")
    suspend fun deleteCartItem(productId: Int)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}