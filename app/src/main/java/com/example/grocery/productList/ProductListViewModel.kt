package com.example.grocery.productList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.domain.models.Product
import com.example.domain.repositories.ProductRepository
import com.example.domain.useCases.GetProductsByCategoryUseCase
import com.example.grocery.states.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val getProductsByCategoryUseCase: GetProductsByCategoryUseCase,
    private val repository: ProductRepository
) : ViewModel() {

    private val _products = MutableStateFlow<PagingData<Product>>(PagingData.empty())
    val products: StateFlow<PagingData<Product>> = _products.asStateFlow()

    private val _uploadImageState = MutableStateFlow<UiState<String>>(UiState.Loading)
    val uploadImageState = _uploadImageState.asStateFlow()

    private val _createProductState = MutableStateFlow<UiState<Product>>(UiState.Loading)
    val createProductState = _createProductState.asStateFlow()

    fun uploadImage(file: File) = viewModelScope.launch {
        _uploadImageState.value = UiState.Loading
        try {
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
            val url = repository.uploadImage(body)
            _uploadImageState.value = UiState.Success(url)
        } catch (e: Exception) {
            _uploadImageState.value = UiState.Error(e.localizedMessage ?: "Image upload failed")
        }
    }

    fun createProduct(product: Product) = viewModelScope.launch {
        _createProductState.value = UiState.Loading
        try {
            val created = repository.addProduct(product)
            _createProductState.value = UiState.Success(created)
        } catch (e: Exception) {
            _createProductState.value = UiState.Error(e.localizedMessage ?: "Product creation failed")
        }
    }
    fun loadProducts(categoryId: Int) {
        viewModelScope.launch {
            getProductsByCategoryUseCase(categoryId)
                .cachedIn(viewModelScope)
                .collect {
                    _products.value = it
                }
        }
    }


}
