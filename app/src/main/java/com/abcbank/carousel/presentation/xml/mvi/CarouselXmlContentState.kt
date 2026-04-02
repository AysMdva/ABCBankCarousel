package com.abcbank.carousel.presentation.xml.mvi

import com.abcbank.carousel.domain.model.ListItem
import com.abcbank.carousel.domain.model.PageData

sealed interface CarouselXmlContentState {
    data object Loading : CarouselXmlContentState
    data object Error : CarouselXmlContentState

    data class Content(
        val pages: List<PageData>,
        val filteredItems: List<ListItem>
    ) : CarouselXmlContentState

    data class Empty(
        val pages: List<PageData>,
        val reason: CarouselXmlEmptyReason
    ) : CarouselXmlContentState
}

enum class CarouselXmlEmptyReason {
    NO_PAGES,
    NO_SEARCH_RESULTS
}
