package com.example.data.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.data.apis.ProductApiService
import com.example.data.mappers.ProductMapper
import com.example.data.paging.ProductPagingSource
import com.example.domain.models.Product
import com.example.domain.repositories.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val api: ProductApiService,
    private val mapper: ProductMapper
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
}
