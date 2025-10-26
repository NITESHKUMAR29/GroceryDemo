package com.example.domain.useCases

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

class SearchNewsUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val networkChecker: NetworkChecker
) {
    operator fun invoke(query: String): Flow<List<Product>> = flow {
        if (!networkChecker.isNetworkConnected()) {
            throw Exception("No internet connection")
        }
        emitAll(
            productRepository.searchProducts(query)
                .map { list -> list.filter { it.title.contains(query, ignoreCase = true) } }
        )
    }.catch { e ->
        throw e
    }.flowOn(Dispatchers.IO)
}