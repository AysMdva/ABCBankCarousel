package com.abcbank.carousel.presentation.xml.model

import com.abcbank.carousel.domain.model.PageStatistics
import java.io.Serializable

data class StatisticsSheetItem(
    val pageNumber: Int,
    val itemCount: Int,
    val topCharacters: List<StatisticsCharacterItem>
) : Serializable

data class StatisticsCharacterItem(
    val character: Char,
    val count: Int
) : Serializable

fun PageStatistics.toSheetItem(): StatisticsSheetItem {
    return StatisticsSheetItem(
        pageNumber = pageNumber,
        itemCount = itemCount,
        topCharacters = topCharacters.map { characterCount ->
            StatisticsCharacterItem(
                character = characterCount.character,
                count = characterCount.count
            )
        }
    )
}
