package com.example.grocery.uis.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.domain.models.Category
import com.example.domain.models.Product
import com.example.grocery.baseSupport.BaseActivity
import com.example.grocery.databinding.ActivityCartBinding
import com.example.grocery.viewModels.ProductListViewModel
import com.example.grocery.uis.adapters.CartAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CartActivity : BaseActivity() {

    private lateinit var binding: ActivityCartBinding
    private val viewModel: ProductListViewModel by viewModels()
    private lateinit var adapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeCart()
    }

    private fun setupRecyclerView() {
        adapter = CartAdapter { item, newQty ->
            viewModel.updateProductQuantity(
                product = Product(
                    id = item.productId,
                    title = item.title,
                    price = item.price ?: 0,
                    description = "",
                    images = listOf(item.image ?: ""),
                    slug = "",
                    category = Category(0, "",),
                    creationAt = "",
                    updatedAt = ""
                ),
                quantity = newQty
            )
        }

        binding.cartRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.cartRecyclerView.adapter = adapter
    }

    private fun observeCart() {
        lifecycleScope.launch {
            viewModel.cartItems.collectLatest { items ->
                adapter.submitList(items)
                val total = items.sumOf { it.quantity * (it.price ?: 0) }
                binding.totalPriceText.text = "Total: ₹$total"
            }
        }
    }
}
