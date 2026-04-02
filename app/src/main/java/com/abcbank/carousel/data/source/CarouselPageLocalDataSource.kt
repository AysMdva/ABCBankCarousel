package com.abcbank.carousel.data.source

import com.abcbank.carousel.data.model.CarouselPageSeed

interface CarouselPageLocalDataSource {
    suspend fun getPageSeeds(): List<CarouselPageSeed>
}
