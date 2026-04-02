package com.abcbank.carousel.di

import android.content.Context
import com.abcbank.carousel.data.repository.DefaultCarouselRepository
import com.abcbank.carousel.data.source.AssetCarouselPageLocalDataSource
import com.abcbank.carousel.domain.repository.CarouselRepository

class CarouselAppContainer(context: Context) {

    private val localDataSource = AssetCarouselPageLocalDataSource(
        context = context.applicationContext
    )

    val carouselRepository: CarouselRepository = DefaultCarouselRepository(localDataSource)
}
