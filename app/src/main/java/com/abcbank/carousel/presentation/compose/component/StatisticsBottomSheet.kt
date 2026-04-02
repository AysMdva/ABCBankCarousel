@file:OptIn(ExperimentalMaterial3Api::class)
package com.abcbank.carousel.presentation.compose.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.abcbank.carousel.domain.model.PageStatistics
import com.abcbank.carousel.util.formatCharacterCounts
import com.abcbank.carousel.R

@Composable
fun StatisticsBottomSheet(
    statistics: List<PageStatistics>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val horizontalPadding = dimensionResource(R.dimen.spacing_xlarge)
    val verticalPadding = dimensionResource(R.dimen.spacing_large)
    val itemSpacing = dimensionResource(R.dimen.spacing_medium)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding, vertical = verticalPadding),
            verticalArrangement = Arrangement.spacedBy(itemSpacing)
        ) {
            items(statistics, key = { it.pageNumber }) { item ->
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.page_label, item.pageNumber),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = pluralStringResource(
                            id = R.plurals.statistics_item_count,
                            count = item.itemCount,
                            item.itemCount
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = context.formatCharacterCounts(item.topCharacters),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
