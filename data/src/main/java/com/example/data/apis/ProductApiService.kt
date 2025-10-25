package com.example.data.apis

import com.example.data.dtos.ProductDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductApiService {

    @GET("api/v1/products/")
    suspend fun getProducts(
        @Query("categoryId") categoryId: Int,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Response<List<ProductDto>>
}