package com.abcbank.carousel.presentation.xml.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abcbank.carousel.R
import com.abcbank.carousel.databinding.ItemStatisticBinding
import com.abcbank.carousel.presentation.xml.model.StatisticsSheetItem
import com.abcbank.carousel.util.formatCharacterCounts

class StatisticsAdapter : ListAdapter<StatisticsSheetItem, StatisticsAdapter.StatisticsViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatisticsViewHolder {
        val binding = ItemStatisticBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return StatisticsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StatisticsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class StatisticsViewHolder(
        private val binding: ItemStatisticBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: StatisticsSheetItem) = with(binding) {
            val rootContext = root.context
            textViewPageName.text = rootContext.getString(R.string.page_label, item.pageNumber)
            textViewItemCount.text = rootContext.resources.getQuantityString(
                R.plurals.statistics_item_count,
                item.itemCount,
                item.itemCount
            )
            textViewTopCharacters.text = rootContext.formatCharacterCounts(item.topCharacters)
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<StatisticsSheetItem>() {
        override fun areItemsTheSame(old: StatisticsSheetItem, new: StatisticsSheetItem) = old.pageNumber == new.pageNumber
        override fun areContentsTheSame(old: StatisticsSheetItem, new: StatisticsSheetItem) = old == new
    }
}
