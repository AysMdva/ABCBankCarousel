package com.abcbank.carousel.util

import android.content.Context
import com.abcbank.carousel.R
import com.abcbank.carousel.presentation.xml.model.StatisticsCharacterItem

fun Context.formatCharacterCounts(characters: List<StatisticsCharacterItem>): String {
    val separator = getString(R.string.statistics_separator)
    return characters.joinToString(separator = separator) { characterCount ->
        getString(
            R.string.character_count_format,
            characterCount.character.toString(),
            characterCount.count
        )
    }
}
