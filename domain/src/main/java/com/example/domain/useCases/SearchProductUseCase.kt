package com.example.domain.useCases


import com.example.domain.repositories.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchNewsUseCase @Inject constructor(private val productRepository: ProductRepository) {
    operator fun invoke(query: String) = productRepository.searchProducts(query)
        .map { list -> list.filter { it.title.contains(query, ignoreCase = true) } }
        .flowOn(Dispatchers.IO)
}