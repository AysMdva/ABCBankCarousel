package com.abcbank.carousel.presentation.xml.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abcbank.carousel.databinding.ItemCarouselImageBinding

class ImageCarouselAdapter : ListAdapter<Int, ImageCarouselAdapter.ImageViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemCarouselImageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ImageViewHolder(
        private val binding: ItemCarouselImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(imageRes: Int) {
            binding.imageViewCarousel.setImageResource(imageRes)
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Int>() {
        override fun areItemsTheSame(old: Int, new: Int) = old == new
        override fun areContentsTheSame(old: Int, new: Int) = old == new
    }
}