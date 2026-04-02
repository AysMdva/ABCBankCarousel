package com.abcbank.carousel.domain.usecase

import com.abcbank.carousel.domain.model.CharacterCount
import com.abcbank.carousel.domain.model.ListItem
import com.abcbank.carousel.domain.model.PageData
import org.junit.Assert.assertEquals
import org.junit.Test

class CalculateStatisticsUseCaseTest {

    private val useCase = CalculateStatisticsUseCase()

    @Test
    fun `returns page item counts and top characters`() {
        val pages = listOf(
            PageData(
                id = 0,
                imageRes = 1,
                items = listOf(
                    ListItem("1", "Alpha", "beta", 1),
                    ListItem("2", "Gamma", "delta", 1)
                )
            )
        )

        val result = useCase(pages)

        assertEquals(1, result.size)
        assertEquals(1, result.first().pageNumber)
        assertEquals(2, result.first().itemCount)
        assertEquals(
            listOf(
                CharacterCount('a', 6),
                CharacterCount('e', 2),
                CharacterCount('l', 2)
            ),
            result.first().topCharacters
        )
    }

    @Test
    fun `ignores non letter characters when calculating top characters`() {
        val pages = listOf(
            PageData(
                id = 0,
                imageRes = 1,
                items = listOf(
                    ListItem("1", "A-1", "B_2", 1)
                )
            )
        )

        val result = useCase(pages)

        assertEquals(
            listOf(
                CharacterCount('a', 1),
                CharacterCount('b', 1)
            ),
            result.first().topCharacters
        )
    }
}
