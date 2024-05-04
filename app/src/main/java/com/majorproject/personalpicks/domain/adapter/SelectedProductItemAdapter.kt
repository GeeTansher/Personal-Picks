package com.majorproject.personalpicks.domain.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.majorproject.personalpicks.data.model.Product
import com.majorproject.personalpicks.databinding.SelectedProductItemBinding

class SelectedProductItemAdapter(private val onItemClick:(Product)->Unit):
    ListAdapter<Product, SelectedProductItemAdapter.SelectedProductItemViewHolder>(
        COMPARATOR
    ){

    inner class SelectedProductItemViewHolder(val binding: SelectedProductItemBinding):
        RecyclerView.ViewHolder(binding.root)

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedProductItemViewHolder {
        return SelectedProductItemViewHolder(
            SelectedProductItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: SelectedProductItemViewHolder, position: Int) {
        val item = getItem(position)

        holder.binding.apply {
            productButton.text = item.product_id
            productButton.setOnCloseIconClickListener{
                onItemClick(item)
            }
        }

    }

}