package com.example.grocery.uis.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope

import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.domain.models.Product
import com.example.grocery.R
import com.example.grocery.databinding.ItemProductBinding
import com.example.grocery.productList.ProductListViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProductPagingAdapter(
    private val viewModel: ProductListViewModel,
    private val lifecycleOwners: LifecycleOwner
) : PagingDataAdapter<Product, ProductPagingAdapter.ProductViewHolder>(DiffCallback) {
    companion object DiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean =
            oldItem == newItem
    }


    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var quantityJob: Job? = null

        @SuppressLint("SetTextI18n")
        fun bind(product: Product) {
            binding.apply {
                titleText.text = product.title
                priceText.text = "₹${product.price}"
                priceText.setTextColor(root.context.getColor(R.color.green))

                Glide.with(root.context)
                    .load(product.images.firstOrNull())
                    .thumbnail(0.1f)
                    .priority(Priority.HIGH)
                    .skipMemoryCache(false)
                    .placeholder(R.color.divider)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(productImage)


                quantityJob?.cancel()


                quantityJob = lifecycleOwners.lifecycleScope.launch {
                    viewModel.getProductQuantity(product.id).collectLatest { qty ->
                        updateQuantityUI(qty)
                    }
                }


                quantityText.setOnClickListener {
                    if (!quantityLayout.isSelected) {
                        viewModel.updateProductQuantity(product, 1)
                    }
                }


                plusButton.setOnClickListener {
                    val currentQty = quantityText.text.toString().toIntOrNull() ?: 0
                    viewModel.updateProductQuantity(product, currentQty + 1)
                }


                minusButton.setOnClickListener {
                    val currentQty = quantityText.text.toString().toIntOrNull() ?: 0
                    val newQty = currentQty - 1
                    viewModel.updateProductQuantity(product, newQty)
                }
            }
        }

        private fun ItemProductBinding.updateQuantityUI(qty: Int) {
            if (qty > 0) {
                quantityLayout.isSelected = true
                quantityText.text = qty.toString()
                quantityText.setTextColor(root.context.getColor(android.R.color.white))
                minusButton.visibility = View.VISIBLE
                plusButton.visibility = View.VISIBLE
            } else {
                quantityLayout.isSelected = false
                quantityText.text = "ADD"
                quantityText.setTextColor(root.context.getColor(R.color.colorAccent))
                minusButton.visibility = View.GONE
                plusButton.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding =
            ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }
}

