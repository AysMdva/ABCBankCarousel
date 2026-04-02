package com.abcbank.carousel.domain.model

data class CharacterCount(
    val character: Char,
    val count: Int
)

data class PageStatistics(
    val pageNumber: Int,
    val itemCount: Int,
    val topCharacters: List<CharacterCount>
)
