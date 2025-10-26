package com.example.data.mappers

import com.example.data.local.entities.CartItemEntity
import com.example.domain.models.CartItem

class CartMapper {

    fun toDomain(entity: CartItemEntity): CartItem = CartItem(
        productId = entity.productId,
        title = entity.title,
        price = entity.price,
        image = entity.image,
        quantity = entity.quantity
    )

    fun toEntity(domain: CartItem): CartItemEntity = CartItemEntity(
        productId = domain.productId,
        title = domain.title,
        price = domain.price,
        image = domain.image,
        quantity = domain.quantity
    )
}