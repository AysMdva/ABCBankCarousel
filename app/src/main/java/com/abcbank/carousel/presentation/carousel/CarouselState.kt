package com.abcbank.carousel.presentation.carousel

import androidx.compose.runtime.Stable
import com.abcbank.carousel.domain.model.PageStatistics

@Stable
data class CarouselState(
    val contentState: CarouselContentState = CarouselContentState.Loading,
    val currentPage: Int = 0,
    val searchQuery: String = "",
    val statistics: List<PageStatistics> = emptyList(),
    val isStatisticsVisible: Boolean = false
) {
    val pages: List<com.abcbank.carousel.domain.model.PageData>
        get() = when (val state = contentState) {
            is CarouselContentState.Content -> state.pages
            is CarouselContentState.Empty -> state.pages
            CarouselContentState.Error,
            CarouselContentState.Loading -> emptyList()
        }

    val filteredItems: List<com.abcbank.carousel.domain.model.ListItem>
        get() = (contentState as? CarouselContentState.Content)?.filteredItems.orEmpty()

    val emptyReason: CarouselEmptyReason?
        get() = (contentState as? CarouselContentState.Empty)?.reason

    val isLoading: Boolean
        get() = contentState is CarouselContentState.Loading

    val isError: Boolean
        get() = contentState is CarouselContentState.Error

    val canShowStatistics: Boolean
        get() = statistics.isNotEmpty()

    val hasResolvedInitialLoad: Boolean
        get() = contentState !is CarouselContentState.Loading && contentState !is CarouselContentState.Error

    val currentItems: List<com.abcbank.carousel.domain.model.ListItem>
        get() = pages.getOrNull(currentPage)?.items.orEmpty()

    val pageCount: Int
        get() = pages.size
}
