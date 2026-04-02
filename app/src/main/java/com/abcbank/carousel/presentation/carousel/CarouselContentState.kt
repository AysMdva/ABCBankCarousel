package com.abcbank.carousel.presentation.carousel

import androidx.compose.runtime.Stable
import com.abcbank.carousel.domain.model.ListItem
import com.abcbank.carousel.domain.model.PageData

@Stable
sealed interface CarouselContentState {
    data object Loading : CarouselContentState
    data object Error : CarouselContentState

    data class Content(
        val pages: List<PageData>,
        val filteredItems: List<ListItem>
    ) : CarouselContentState

    data class Empty(
        val pages: List<PageData>,
        val reason: CarouselEmptyReason
    ) : CarouselContentState
}

enum class CarouselEmptyReason {
    NO_PAGES,
    NO_SEARCH_RESULTS
}
