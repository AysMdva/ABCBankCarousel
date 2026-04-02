package com.abcbank.carousel.presentation.xml.mvi

import com.abcbank.carousel.presentation.xml.model.StatisticsSheetItem

sealed interface CarouselXmlEffect {
    data object ShowLoadError : CarouselXmlEffect
    data object ScrollItemsToTop : CarouselXmlEffect
    data class ShowStatistics(val statistics: List<StatisticsSheetItem>) : CarouselXmlEffect
}
