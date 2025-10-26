package com.example.domain.useCases

import com.example.domain.models.Category
import com.example.domain.models.Product
import com.example.domain.repositories.ProductRepository
import com.example.domain.utility.NetworkChecker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductCategoryUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val networkChecker: NetworkChecker
) {
    operator fun invoke(): Flow<List<Category>> = flow {
        if (!networkChecker.isNetworkConnected()) {
            throw Exception("No internet connection")
        }
        emitAll(
            productRepository.getProductCategories()
        )
    }.catch { e ->
        throw e
    }.flowOn(Dispatchers.IO)
}