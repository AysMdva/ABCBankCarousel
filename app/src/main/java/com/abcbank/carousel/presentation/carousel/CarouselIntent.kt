package com.abcbank.carousel.presentation.carousel

sealed interface CarouselIntent {
    data object LoadData : CarouselIntent
    data object RetryLoadClicked : CarouselIntent
    data class PageChanged(val page: Int) : CarouselIntent
    data class SearchQueryChanged(val query: String) : CarouselIntent
    data object StatisticsClicked : CarouselIntent
    data object StatisticsDismissed : CarouselIntent
}
