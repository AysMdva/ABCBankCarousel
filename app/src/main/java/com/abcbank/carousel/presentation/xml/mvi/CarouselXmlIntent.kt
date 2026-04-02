package com.abcbank.carousel.presentation.xml.mvi

sealed interface CarouselXmlIntent {
    data object LoadData : CarouselXmlIntent
    data object RetryLoadClicked : CarouselXmlIntent
    data class PageChanged(val page: Int) : CarouselXmlIntent
    data class SearchQueryChanged(val query: String) : CarouselXmlIntent
    data object StatisticsClicked : CarouselXmlIntent
}
