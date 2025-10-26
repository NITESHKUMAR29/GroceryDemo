package com.example.domain.repositories

import androidx.paging.PagingData
import com.example.domain.models.Product
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

interface ProductRepository {
    fun getProductsByCategory(categoryId: Int): Flow<PagingData<Product>>
    suspend fun addProduct(product: Product): Product
    suspend fun uploadImage(image: MultipartBody.Part): String
    fun getAllProducts(): Flow<PagingData<Product>>
    fun searchProducts(query: String): Flow<List<Product>>
}
