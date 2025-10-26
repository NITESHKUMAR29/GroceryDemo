package com.example.data.mappers

import com.example.data.dtos.CategoryDto
import com.example.domain.models.Category

class ProductCategoryMapper {
    fun toDomain(dto: CategoryDto): Category = Category(
        id = dto.id ?: 0,
        name = dto.name ?: ""
    )

    fun toDto(domain: Category): CategoryDto = CategoryDto(
        id = domain.id,
        name = domain.name
    )


}