package com.abcbank.carousel.data.repository

import com.abcbank.carousel.data.source.CarouselPageLocalDataSource
import com.abcbank.carousel.domain.model.ListItem
import com.abcbank.carousel.domain.model.PageData
import com.abcbank.carousel.domain.repository.CarouselRepository
import java.util.Locale

class DefaultCarouselRepository(
    private val localDataSource: CarouselPageLocalDataSource
) : CarouselRepository {

    override suspend fun getPages(): List<PageData> {
        return localDataSource.getPageSeeds().mapIndexed { pageIndex, seed ->
            PageData(
                id = pageIndex,
                imageRes = seed.imageRes,
                items = List(seed.itemCount) { itemIndex ->
                    ListItem(
                        id = "$pageIndex$ITEM_ID_SEPARATOR$itemIndex",
                        title = String.format(Locale.US, ITEM_TITLE_FORMAT, pageIndex + 1, itemIndex + 1),
                        subtitle = seed.tags.joinToString(separator = ITEM_TAG_SEPARATOR),
                        thumbnailRes = seed.imageRes
                    )
                }
            )
        }
    }

    private companion object {
        const val ITEM_ID_SEPARATOR = "_"
        const val ITEM_TAG_SEPARATOR = " "
        const val ITEM_TITLE_FORMAT = "Page %d - Item %d"
    }
}
