package com.abcbank.carousel.presentation.carousel

sealed interface CarouselEffect {
    data object ShowLoadError : CarouselEffect
}
