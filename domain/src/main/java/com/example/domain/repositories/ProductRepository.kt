package com.example.domain.repositories

import androidx.paging.PagingData
import com.example.domain.models.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProductsByCategory(categoryId: Int): Flow<PagingData<Product>>
}
