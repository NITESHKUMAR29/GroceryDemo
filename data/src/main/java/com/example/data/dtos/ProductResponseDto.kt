package com.example.data.dtos

data class ProductDto(
    val id: Int?,
    val title: String?,
    val slug: String?,
    val price: Int?,
    val description: String?,
    val categoryId: Int?,
    val images: List<String>?,
    val creationAt: String?,
    val updatedAt: String?
)

data class CategoryDto(
    val id: Int?,
    val name: String?,
    val slug: String?,
    val image: String?,
    val creationAt: String?,
    val updatedAt: String?
)

