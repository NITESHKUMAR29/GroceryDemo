package com.example.grocery.uis.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.example.grocery.R
import com.example.grocery.databinding.FragmentGroceryBinding
import com.example.grocery.productList.ProductListViewModel
import com.example.grocery.uis.adapters.ProductLoadStateAdapter
import com.example.grocery.uis.adapters.ProductPagingAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class GroceryFragment : Fragment() {
    lateinit var binding: FragmentGroceryBinding
    private val viewModel: ProductListViewModel by activityViewModels()
    private lateinit var adapter: ProductPagingAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_grocery, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        observeProducts()

    }

    override fun onResume() {
        super.onResume()
        viewModel.loadProducts(categoryId = 47)
    }

    private fun setupAdapter() {
        adapter = ProductPagingAdapter()


        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
            header = ProductLoadStateAdapter { adapter.retry() },
            footer = ProductLoadStateAdapter { adapter.retry() }
        )

        binding.retryButton.setOnClickListener { adapter.retry() }

        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadStates ->
                binding.progressBar.isVisible = loadStates.refresh is LoadState.Loading
                binding.recyclerView.isVisible = loadStates.refresh is LoadState.NotLoading
                binding.retryButton.isVisible = loadStates.refresh is LoadState.Error
            }
        }
    }


    private fun observeProducts() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.products.collectLatest { pagingData ->
                    adapter.submitData(pagingData)
                }
            }
        }
    }

}