package com.abcbank.carousel.domain.repository

import com.abcbank.carousel.domain.model.PageData

interface CarouselRepository {
    suspend fun getPages(): List<PageData>
}
