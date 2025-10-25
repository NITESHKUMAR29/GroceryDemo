package com.example.domain.useCases

import androidx.paging.PagingData
import com.example.domain.models.Product
import com.example.domain.repositories.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetProductsByCategoryUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(categoryId: Int): Flow<PagingData<Product>> =
        repository.getProductsByCategory(categoryId).flowOn(Dispatchers.IO)
}