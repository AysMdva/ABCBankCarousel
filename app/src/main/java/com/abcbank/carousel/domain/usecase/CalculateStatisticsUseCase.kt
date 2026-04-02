package com.abcbank.carousel.domain.usecase

import com.abcbank.carousel.domain.model.CharacterCount
import com.abcbank.carousel.domain.model.PageData
import com.abcbank.carousel.domain.model.PageStatistics

class CalculateStatisticsUseCase {

    operator fun invoke(pages: List<PageData>): List<PageStatistics> {
        return pages.mapIndexed { i, page ->
            val text = page.items.joinToString("") { "${it.title}${it.subtitle}" }
            PageStatistics(
                pageNumber = i + 1,
                itemCount = page.items.size,
                topCharacters = topChars(text)
            )
        }
    }

    private fun topChars(text: String): List<CharacterCount> {
        return text
            .filter(Char::isLetter)
            .lowercase()
            .groupingBy { it }
            .eachCount()
            .entries
            .sortedWith(compareByDescending<Map.Entry<Char, Int>> { it.value }.thenBy { it.key })
            .take(3)
            .map { (char, count) -> CharacterCount(character = char, count = count) }
    }
}
