package com.abcbank.carousel.presentation.xml.mvi

data class CarouselXmlState(
    val contentState: CarouselXmlContentState = CarouselXmlContentState.Loading,
    val currentPage: Int = 0,
    val searchQuery: String = ""
) {
    val pages
        get() = when (val state = contentState) {
            is CarouselXmlContentState.Content -> state.pages
            is CarouselXmlContentState.Empty -> state.pages
            CarouselXmlContentState.Error,
            CarouselXmlContentState.Loading -> emptyList()
        }

    val filteredItems
        get() = (contentState as? CarouselXmlContentState.Content)?.filteredItems.orEmpty()

    val emptyReason: CarouselXmlEmptyReason?
        get() = (contentState as? CarouselXmlContentState.Empty)?.reason

    val canShowStatistics: Boolean
        get() = pages.isNotEmpty()

    val hasResolvedInitialLoad: Boolean
        get() = contentState !is CarouselXmlContentState.Loading && contentState !is CarouselXmlContentState.Error

    val currentItems
        get() = pages.getOrNull(currentPage)?.items.orEmpty()

    val pageCount: Int
        get() = pages.size
}
