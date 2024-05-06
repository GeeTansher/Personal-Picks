package com.majorproject.personalpicks.domain.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.majorproject.personalpicks.databinding.CustomerItemBinding

class CustomerIdItemAdapter(private val onItemClick:(String)->Unit) :
    ListAdapter<String, CustomerIdItemAdapter.CustomerIdItemViewHolder>(
        COMPARATOR
    ) {

    inner class CustomerIdItemViewHolder(val binding: CustomerItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerIdItemViewHolder {
        return CustomerIdItemViewHolder(
            CustomerItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CustomerIdItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.apply {
            customerIdTextView.text = item
            selectButton.setOnClickListener {
                onItemClick(item)
            }
        }
    }

}

