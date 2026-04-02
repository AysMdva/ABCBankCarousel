package com.abcbank.carousel.domain.model

data class PageData(
    val id: Int,
    val imageRes: Int,
    val items: List<ListItem>
)
