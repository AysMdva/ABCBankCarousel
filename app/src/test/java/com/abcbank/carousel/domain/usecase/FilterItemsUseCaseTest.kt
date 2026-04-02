package com.abcbank.carousel.domain.usecase

import com.abcbank.carousel.domain.model.ListItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FilterItemsUseCaseTest {

    private val useCase = FilterItemsUseCase()
    private val items = listOf(
        ListItem("1", "Savings", "apple banana", 1),
        ListItem("2", "Insurance", "orange grape", 1),
        ListItem("3", "Travel", "Berry Rewards", 1)
    )

    @Test
    fun `returns all items when query is blank`() {
        val result = useCase(items, " ")

        assertEquals(items, result)
    }

    @Test
    fun `matches title ignoring case`() {
        val result = useCase(items, "sav")

        assertEquals(listOf(items.first()), result)
    }

    @Test
    fun `matches subtitle ignoring case`() {
        val result = useCase(items, "berry")

        assertEquals(listOf(items.last()), result)
    }

    @Test
    fun `returns empty list when query has no matches`() {
        val result = useCase(items, "mortgage")

        assertTrue(result.isEmpty())
    }
}
