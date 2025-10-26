package com.example.data.apis

import com.example.data.dtos.CategoryDto
import com.example.data.dtos.ProductDto
import com.example.data.dtos.UploadImageResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ProductApiService {

    @GET("api/v1/products/")
    suspend fun getProducts(
        @Query("categoryId") categoryId: Int,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Response<List<ProductDto>>

    @GET("api/v1/products/")
    suspend fun getAllProduct(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Response<List<ProductDto>>

    @GET("api/v1/products/")
    suspend fun searchProduct(
    ): Response<List<ProductDto>>

    @GET("/api/v1/categories/")
    suspend fun getCategories(
    ): Response<List<CategoryDto>>

    @Multipart
    @POST("api/v1/files/upload")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): Response<UploadImageResponse>

    @POST("api/v1/products/")
    suspend fun addProduct(
        @Body productDto: ProductDto
    ): Response<ProductDto>


}