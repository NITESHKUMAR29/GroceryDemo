package com.example.grocery.uis.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.domain.models.CartItem
import com.example.grocery.R
import com.example.grocery.databinding.ItemCartBinding

class CartAdapter(
    private val onQuantityChanged: (CartItem, Int) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(DiffCallback) {

    object DiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem) =
            oldItem.productId == newItem.productId

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem) =
            oldItem == newItem
    }

    inner class CartViewHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: CartItem) {
            binding.apply {
                titleText.text = item.title
                priceText.text = "₹${item.price ?: 0}"
                quantityText.text = item.quantity.toString()

                Glide.with(root.context)
                    .load(item.image)
                    .placeholder(R.color.divider)
                    .into(productImage)

                plusButton.setOnClickListener {
                    val newQty = item.quantity + 1
                    onQuantityChanged(item, newQty)
                }

                minusButton.setOnClickListener {
                    val newQty = item.quantity - 1
                    onQuantityChanged(item, newQty)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }
}
