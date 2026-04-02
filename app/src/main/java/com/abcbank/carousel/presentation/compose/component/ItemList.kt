@file:OptIn(ExperimentalFoundationApi::class)
package com.abcbank.carousel.presentation.compose.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.abcbank.carousel.domain.model.ListItem
import com.abcbank.carousel.R

@Composable
fun ItemList(
    items: List<ListItem>,
    listState: LazyListState = rememberLazyListState(),
    topContent: (@Composable () -> Unit)? = null,
    stickySearchContent: (@Composable () -> Unit)? = null,
    emptyContent: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val itemSpacing = dimensionResource(R.dimen.spacing_xsmall)
    val rowHorizontalPadding = dimensionResource(R.dimen.spacing_xlarge)
    val rowVerticalPadding = dimensionResource(R.dimen.spacing_medium_plus)
    val iconSize = dimensionResource(R.dimen.list_icon_size)
    val textStartPadding = dimensionResource(R.dimen.spacing_large)

    LazyColumn(
        state = listState,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(itemSpacing)
    ) {
        if (topContent != null) {
            item {
                topContent()
            }
        }
        if (stickySearchContent != null) {
            stickyHeader {
                Surface(color = MaterialTheme.colorScheme.surface) {
                    stickySearchContent()
                }
            }
        }
        if (items.isEmpty()) {
            if (emptyContent != null) {
                item {
                    emptyContent()
                }
            }
        } else {
            items(
                items = items,
                key = { it.id }
            ) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = rowHorizontalPadding, vertical = rowVerticalPadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = item.thumbnailRes),
                        contentDescription = null,
                        modifier = Modifier.size(iconSize),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                    )
                    Column(
                        modifier = Modifier.padding(start = textStartPadding)
                    ) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = item.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
