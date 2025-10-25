package com.example.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.data.apis.ProductApiService
import com.example.data.mappers.ProductMapper
import com.example.domain.models.Product

class ProductPagingSource(
    private val api: ProductApiService,
    private val mapper: ProductMapper,
    private val categoryId: Int? = null
) : PagingSource<Int, Product>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        val offset = params.key ?: 0
        val limit = params.loadSize

        return try {
            val response = if (categoryId == null) {
                api.getAllProduct(offset, limit)
            } else {
                api.getProducts(categoryId, offset, limit)
            }

            if (response.isSuccessful) {
                val body = response.body().orEmpty()
                val products = body.map { mapper.toDomain(it) }

                LoadResult.Page(
                    data = products,
                    prevKey = if (offset == 0) null else offset - limit,
                    nextKey = if (products.isEmpty()) null else offset + limit
                )
            } else {
                LoadResult.Error(Exception("API Error: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        return state.anchorPosition?.let { pos ->
            state.closestPageToPosition(pos)?.prevKey?.plus(state.config.pageSize)
                ?: state.closestPageToPosition(pos)?.nextKey?.minus(state.config.pageSize)
        }
    }
}
