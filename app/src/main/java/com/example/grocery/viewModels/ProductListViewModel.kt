package com.example.grocery.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.domain.models.CartItem
import com.example.domain.models.Category
import com.example.domain.models.Product
import com.example.domain.repositories.CartRepository
import com.example.domain.repositories.ProductRepository
import com.example.domain.useCases.GetProductsByCategoryUseCase
import com.example.domain.useCases.ProductCategoryUseCase
import com.example.domain.useCases.SearchNewsUseCase
import com.example.grocery.states.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val getProductsByCategoryUseCase: GetProductsByCategoryUseCase,
    private val searchNewsUseCase: SearchNewsUseCase,
    private val productCategoryUseCase: ProductCategoryUseCase,

    private val repository: ProductRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _products = MutableStateFlow<PagingData<Product>>(PagingData.empty())
    val products: StateFlow<PagingData<Product>> = _products.asStateFlow()

    private val _uploadImageState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val uploadImageState = _uploadImageState.asStateFlow()

    private val _createProductState = MutableStateFlow<UiState<Product>>(UiState.Idle)
    val createProductState = _createProductState.asStateFlow()

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _searchProductState = MutableStateFlow<UiState<List<Product>>>(UiState.Idle)
    val searchProductState: StateFlow<UiState<List<Product>>> = _searchProductState

    private val _productCategoryState = MutableStateFlow<UiState<List<Category>>>(UiState.Idle)
    val productCategoryState: StateFlow<UiState<List<Category>>> = _productCategoryState

    init {
        observeCartItems()
    }


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
            _createProductState.value =
                UiState.Error(e.localizedMessage ?: "Product creation failed")
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

    fun getProductQuantity(productId: Int): Flow<Int> {
        return cartRepository.getCartItem(productId).map { it?.quantity ?: 0 }
    }

    fun updateProductQuantity(product: Product, quantity: Int) = viewModelScope.launch {
        val item = CartItem(
            productId = product.id,
            title = product.title,
            price = product.price,
            image = product.images.firstOrNull(),
            quantity = quantity
        )
        cartRepository.updateCartItem(item)
    }

    private fun observeCartItems() {
        viewModelScope.launch {
            cartRepository.getAllCartItems().collect { items ->
                _cartItems.value = items
            }
        }
    }

    fun searchProducts(query: String) = viewModelScope.launch {
        Log.d("searchProductQuery", query.isNotEmpty().toString())
        if (query.isNotEmpty()) {
            searchNewsUseCase(query)
                .onStart { _searchProductState.value = UiState.Loading }
                .catch { _searchProductState.value = UiState.Error(it.message.toString()) }
                .collect { _searchProductState.value = UiState.Success(it) }
        }
    }

    fun getCategories() = viewModelScope.launch {
            productCategoryUseCase()
                .onStart { _productCategoryState.value = UiState.Loading }
                .catch { _productCategoryState.value = UiState.Error(it.message.toString()) }
                .collect { _productCategoryState.value = UiState.Success(it) }

    }

}
