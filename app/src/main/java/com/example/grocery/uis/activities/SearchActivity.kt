package com.example.grocery.uis.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.example.grocery.R
import com.example.grocery.baseSupport.BaseActivity
import com.example.grocery.databinding.ActivitySearchBinding
import com.example.grocery.states.UiState
import com.example.grocery.uis.adapters.SearchProductAdapters
import com.example.grocery.viewModels.ProductListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchActivity : BaseActivity() {
    lateinit var binding: ActivitySearchBinding
    private val viewModel: ProductListViewModel by viewModels()
    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_search)

        observeSearch()
        observeCart()

        binding.apply {
            binding.searchEvent.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {

                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    val query = p0.toString()
                    debounceSearch(query)

                }
            })
        }

    }

    private fun observeSearch() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchProductState.collect { state ->
                    Log.d("searchProductState", state.toString())
                    when (state) {
                        is UiState.Error -> {
                            Toast.makeText(this@SearchActivity, state.message, Toast.LENGTH_SHORT).show()
                            binding.progressBar.isVisible = false
                            binding.recyclerView.isVisible = false

                        }

                        UiState.Loading -> {
                            binding.progressBar.isVisible = true
                            binding.recyclerView.isVisible = false
                        }

                        is UiState.Success -> {
                            Log.d("searchProductsss", state.data.toString())
                            val adapter =
                                SearchProductAdapters(state.data, viewModel, this@SearchActivity)
                            binding.recyclerView.layoutManager =
                                GridLayoutManager(this@SearchActivity, 3)
                            binding.recyclerView.adapter = adapter
                            binding.progressBar.isVisible = false
                            binding.recyclerView.isVisible = true
                        }

                        else -> {}
                    }
                }
            }

        }

    }

    private fun observeCart() {
        lifecycleScope.launch {
            viewModel.cartItems.collectLatest { cartItems ->
                val itemCount = cartItems.size
                binding.cartQuantityText.text = "$itemCount Items"

                updateMiniCartVisibility(itemCount > 0)

                binding.viewCartButton.setOnClickListener {
                    val intent = Intent(this@SearchActivity, CartActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun debounceSearch(query: String) {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            delay(500)
            if (query.isNotEmpty()) {
                Log.d("searchProductsss", query)
                viewModel.searchProducts(query)
            }
        }
    }

    private fun updateMiniCartVisibility(show: Boolean) {
        val layout = binding.bottomCartLayout
        if (show && layout.isGone) {
            layout.alpha = 0f
            layout.translationY = layout.height.toFloat()
            layout.visibility = View.VISIBLE
            layout.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(300)
                .start()
        } else if (!show && layout.isVisible) {
            layout.animate()
                .alpha(0f)
                .translationY(layout.height.toFloat())
                .setDuration(300)
                .withEndAction { layout.visibility = View.GONE }
                .start()
        }
    }

}