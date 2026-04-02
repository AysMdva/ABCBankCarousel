package com.abcbank.carousel.presentation.xml.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abcbank.carousel.databinding.ItemListBinding
import com.abcbank.carousel.domain.model.ListItem

class ListItemAdapter : ListAdapter<ListItem, ListItemAdapter.ItemViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ItemViewHolder(
        private val binding: ItemListBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ListItem) = with(binding) {
            textViewTitle.text = item.title
            textViewSubtitle.text = item.subtitle
            imageViewThumbnail.setImageResource(item.thumbnailRes)
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<ListItem>() {
        override fun areItemsTheSame(old: ListItem, new: ListItem) = old.id == new.id
        override fun areContentsTheSame(old: ListItem, new: ListItem) = old == new
    }
}