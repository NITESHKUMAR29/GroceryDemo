package com.example.data.mappers


import com.example.data.dtos.CategoryDto
import com.example.data.dtos.ProductDto
import com.example.domain.models.Category
import com.example.domain.models.Product


class ProductMapper {

    fun toDomain(dto: ProductDto): Product = Product(
        id = dto.id ?: 0,
        title = dto.title ?: "",
        slug = dto.slug ?: "",
        price = dto.price ?: 0,
        description = dto.description ?: "",
        category = Category(dto.categoryId ?: 0, "", "", "", "", ""),
        images = dto.images ?: emptyList(),
        creationAt = dto.creationAt ?: "",
        updatedAt = dto.updatedAt ?: ""
    )

    private fun toDomain(categoryDto: CategoryDto): Category = Category(
        id = categoryDto.id ?: 0,
        name = categoryDto.name ?: "",
        slug = categoryDto.slug ?: "",
        image = categoryDto.image ?: "",
        creationAt = categoryDto.creationAt ?: "",
        updatedAt = categoryDto.updatedAt ?: ""
    )

    fun toDto(domain: Product): ProductDto = ProductDto(
        id = domain.id,
        title = domain.title,
        slug = domain.slug,
        price = domain.price,
        description = domain.description,
        categoryId = domain.category.id,
        images = domain.images,
        creationAt = domain.creationAt,
        updatedAt = domain.updatedAt
    )

    private fun toDto(category: Category): CategoryDto = CategoryDto(
        id = category.id,
        name = category.name,
        slug = category.slug,
        image = category.image,
        creationAt = category.creationAt,
        updatedAt = category.updatedAt
    )
}
