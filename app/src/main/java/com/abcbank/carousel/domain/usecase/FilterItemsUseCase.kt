package com.abcbank.carousel.domain.usecase

import com.abcbank.carousel.domain.model.ListItem

class FilterItemsUseCase {

    operator fun invoke(items: List<ListItem>, query: String): List<ListItem> {
        val q = query.trim()
        if (q.isEmpty()) return items

        return items.filter {
            it.title.contains(q, ignoreCase = true) ||
                    it.subtitle.contains(q, ignoreCase = true)
        }
    }
}