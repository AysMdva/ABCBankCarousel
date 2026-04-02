@file:OptIn(ExperimentalFoundationApi::class)

package com.abcbank.carousel.presentation.compose.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import com.abcbank.carousel.domain.model.PageData
import com.abcbank.carousel.R

@Composable
fun ImageCarousel(
    pages: List<PageData>,
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {
    val horizontalSpacing = dimensionResource(R.dimen.spacing_xlarge)
    val verticalSpacing = dimensionResource(R.dimen.spacing_large)
    val smallSpacing = dimensionResource(R.dimen.spacing_small)
    val mediumSpacing = dimensionResource(R.dimen.spacing_medium)
    val indicatorSize = dimensionResource(R.dimen.page_indicator_size)
    val carouselHeight = dimensionResource(R.dimen.carousel_height)

    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(carouselHeight)
        ) { pageIndex ->
            val page = pages[pageIndex]
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalSpacing, vertical = verticalSpacing),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = page.imageRes),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = smallSpacing, bottom = mediumSpacing),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pages.size) { index ->
                val selected = index == pagerState.currentPage
                Box(
                    modifier = Modifier
                        .padding(horizontal = smallSpacing)
                        .size(indicatorSize)
                        .clip(CircleShape)
                        .background(
                            if (selected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outline
                        )
                )
            }
        }
    }
}
