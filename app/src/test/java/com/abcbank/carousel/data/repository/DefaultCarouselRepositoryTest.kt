package com.abcbank.carousel.data.repository

import com.abcbank.carousel.data.model.CarouselPageSeed
import com.abcbank.carousel.data.source.CarouselPageLocalDataSource
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class DefaultCarouselRepositoryTest {

    @Test
    fun `maps local page seeds into page data`() = runTest {
        val repository = DefaultCarouselRepository(
            localDataSource = FakeCarouselPageLocalDataSource(
                seeds = listOf(
                    CarouselPageSeed(
                        imageRes = 42,
                        itemCount = 2,
                        tags = listOf("apple", "banana")
                    )
                )
            )
        )

        val result = repository.getPages()

        assertEquals(1, result.size)
        assertEquals(0, result.first().id)
        assertEquals(42, result.first().imageRes)
        assertEquals(2, result.first().items.size)
        assertEquals("Page 1 - Item 1", result.first().items[0].title)
        assertEquals("apple banana", result.first().items[0].subtitle)
        assertEquals("0_1", result.first().items[1].id)
    }

    private class FakeCarouselPageLocalDataSource(
        private val seeds: List<CarouselPageSeed>
    ) : CarouselPageLocalDataSource {

        override suspend fun getPageSeeds(): List<CarouselPageSeed> = seeds
    }
}
