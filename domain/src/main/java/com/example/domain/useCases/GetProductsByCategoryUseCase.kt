package com.example.domain.useCases

import androidx.paging.PagingData
import com.example.domain.models.Product
import com.example.domain.repositories.ProductRepository
import com.example.domain.utility.NetworkChecker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


class GetProductsByCategoryUseCase @Inject constructor(
    private val repository: ProductRepository,
    private val networkChecker: NetworkChecker
) {
    operator fun invoke(categoryId: Int?): Flow<PagingData<Product>> {
        return if (!networkChecker.isNetworkConnected()) {
            flow { emit(PagingData.empty()) }
        } else {
            if (categoryId == null || categoryId == 0) {
                repository.getAllProducts()
                    .flowOn(Dispatchers.IO)
            } else {
                repository.getProductsByCategory(categoryId)
                    .flowOn(Dispatchers.IO)
            }
        }
    }
}


