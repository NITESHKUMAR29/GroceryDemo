package com.example.data.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.data.apis.ProductApiService
import com.example.data.mappers.ProductCategoryMapper
import com.example.data.mappers.ProductMapper
import com.example.data.paging.ProductPagingSource
import com.example.domain.models.Category
import com.example.domain.models.Product
import com.example.domain.repositories.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val api: ProductApiService,
    private val mapper: ProductMapper,
    private val productCategoryMapper: ProductCategoryMapper,
) : ProductRepository {

    override fun getProductsByCategory(categoryId: Int): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                prefetchDistance = 2
            ),
            pagingSourceFactory = { ProductPagingSource(api, mapper, categoryId) }
        ).flow
    }

    override suspend fun uploadImage(image: MultipartBody.Part): String {
        val response = api.uploadImage(image)
        if (response.isSuccessful) {
            return response.body()?.location
                ?: throw Exception("Upload failed: location is null")
        } else {
            throw Exception("Upload failed: ${response.message()}")
        }
    }

    override fun getAllProducts(): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                prefetchDistance = 2
            ),
            pagingSourceFactory = { ProductPagingSource(api, mapper) }
        ).flow
    }

    override fun searchProducts(query: String): Flow<List<Product>> {
        return flow {
            val response = api.searchProduct()
            try {
                if (response.isSuccessful) {
                    val products = response.body()?.map { mapper.toDomain(it) }
                    if (products != null) {
                        emit(products)
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    throw Exception("API Error: $errorBody")

                }
            } catch (e: Exception) {
                throw e
            }

        }


    }

    override fun getProductCategories(): Flow<List<Category>> {
        return flow {
            val response = api.getCategories()

            try {
                if (response.isSuccessful) {
                    val categories = response.body()?.map { productCategoryMapper.toDomain(it) }
                    if (categories != null) {
                        emit(categories)
                    } else {
                        val errorBody = response.errorBody()?.string()
                        throw Exception("API Error: $errorBody")
                    }
                }
            } catch (e: Exception) {
                throw e
            }
        }
    }

    override suspend fun addProduct(product: Product): Product {
        val dto = mapper.toDto(product)
        val response = api.addProduct(dto)
        if (response.isSuccessful) {
            return mapper.toDomain(response.body()!!)
        } else {

            val errorBody = response.errorBody()?.string()
            throw Exception("Product creation failed: $errorBody")
        }
    }

}
