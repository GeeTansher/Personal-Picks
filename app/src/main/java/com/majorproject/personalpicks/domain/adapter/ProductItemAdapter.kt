package com.majorproject.personalpicks.domain.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.majorproject.personalpicks.R
import com.majorproject.personalpicks.data.model.Product
import com.majorproject.personalpicks.databinding.ProductItemBinding

class ProductItemAdapter(private val onItemClick:(Product)->Unit) :
    ListAdapter<Product, ProductItemAdapter.ProductItemViewHolder>(
    COMPARATOR
    ){

        inner class ProductItemViewHolder(val binding: ProductItemBinding):
            RecyclerView.ViewHolder(binding.root){}

        companion object {
            private val COMPARATOR = object : DiffUtil.ItemCallback<Product>() {
                override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
                    return oldItem.product_id == newItem.product_id
                }

                override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
                    return oldItem == newItem
                }

            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductItemViewHolder {
            return ProductItemViewHolder(
                ProductItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }


        override fun onBindViewHolder(holder: ProductItemViewHolder, position: Int) {
            val item = getItem(position)

            holder.binding.apply {
                productIdText.text = item.product_id
                productNameText.text = item.product_title
                categoryText.text = item.product_category

                when (item.product_category){
                    "Apparel" ->
                        productImageView.setImageResource(R.drawable.apparel)

                    "Beauty" ->
                        productImageView.setImageResource(R.drawable.beauty)

                    "Gift Card" ->
                        productImageView.setImageResource(R.drawable.giftcard)

                    "Jewelry" ->
                        productImageView.setImageResource(R.drawable.jewelry)

                    "Watches" ->
                        productImageView.setImageResource(R.drawable.watches)

                    "Shoes" ->
                        productImageView.setImageResource(R.drawable.shoes)

                    "Luggage" ->
                        productImageView.setImageResource(R.drawable.luggage)

                    "Global" ->
                        productImageView.setImageResource(R.drawable.global)

                }
                root.setOnClickListener {
                    onItemClick(item)
                }
            }

        }

    }