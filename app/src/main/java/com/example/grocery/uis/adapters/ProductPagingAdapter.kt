package com.example.grocery.uis.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class ProductPagingAdapter :
    PagingDataAdapter<Product, ProductPagingAdapter.ProductViewHolder>(DiffCallback) {

    object DiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean =
            oldItem == newItem
    }

    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.apply {
                titleText.text = product.title
                priceText.text = "₹${product.price}"
                priceText.setTextColor(root.context.getColor(R.color.green))


                Glide.with(root.context)
                    .load(product.images.firstOrNull())
                    .thumbnail(
                        Glide.with(root.context).load(product.images.firstOrNull())
                    )
                    .priority(Priority.HIGH)
                    .skipMemoryCache(false)
                    .placeholder(R.color.divider)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.color.divider)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(productImage)

                quantityLayout.isSelected = false
                quantityText.text = "ADD"
                minusButton.visibility = View.GONE
                plusButton.visibility = View.GONE


                quantityText.setOnClickListener {
                    if (!quantityLayout.isSelected) {
                        quantityLayout.isSelected = true
                        quantityText.text = "1"
                        quantityText.setTextColor(root.context.getColor(android.R.color.white))
                        minusButton.visibility = View.VISIBLE
                        plusButton.visibility = View.VISIBLE
                    }
                }


                plusButton.setOnClickListener {
                    val currentQty = quantityText.text.toString().toIntOrNull() ?: 1
                    quantityText.text = (currentQty + 1).toString()
                }


                minusButton.setOnClickListener {
                    val currentQty = quantityText.text.toString().toIntOrNull() ?: 1
                    if (currentQty > 1) {
                        quantityText.text = (currentQty - 1).toString()
                    } else {
                        quantityLayout.isSelected = false
                        quantityText.text = "ADD"
                        quantityText.setTextColor(root.context.getColor(R.color.colorAccent))
                        minusButton.visibility = View.GONE
                        plusButton.visibility = View.GONE
                    }
                }
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

