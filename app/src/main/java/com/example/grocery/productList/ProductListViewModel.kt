package com.example.grocery.productList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.domain.models.Product
import com.example.domain.useCases.GetProductsByCategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val getProductsByCategoryUseCase: GetProductsByCategoryUseCase
) : ViewModel() {

    private val _products = MutableStateFlow<PagingData<Product>>(PagingData.empty())
    val products: StateFlow<PagingData<Product>> = _products.asStateFlow()

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
