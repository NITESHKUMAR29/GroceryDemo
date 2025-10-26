package com.example.data.repositories

import com.example.data.local.dao.CartDao
import com.example.data.mappers.CartMapper
import com.example.domain.models.CartItem
import com.example.domain.repositories.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class CartRepositoryImpl @Inject constructor(
    private val cartDao: CartDao,
    private val mapper: CartMapper
) : CartRepository {

    override fun getCartItem(productId: Int): Flow<CartItem?> =
        cartDao.getCartItem(productId).map { it?.let(mapper::toDomain) }

    override fun getAllCartItems(): Flow<List<CartItem>> =
        cartDao.getAllCartItems().map { list -> list.map(mapper::toDomain) }

    override suspend fun updateCartItem(item: CartItem) {
        if (item.quantity > 0)
            cartDao.insertCartItem(mapper.toEntity(item))
        else
            cartDao.deleteCartItem(item.productId)
    }

    override suspend fun removeItem(productId: Int) = cartDao.deleteCartItem(productId)

    override suspend fun clearCart() = cartDao.clearCart()
}