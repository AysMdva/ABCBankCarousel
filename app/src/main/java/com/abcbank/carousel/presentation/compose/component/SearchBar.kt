@file:OptIn(ExperimentalFoundationApi::class)
package com.abcbank.carousel.presentation.compose.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.abcbank.carousel.R

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val horizontalPadding = dimensionResource(R.dimen.spacing_xlarge)

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding),
        singleLine = true,
        label = { Text(stringResource(R.string.search_hint)) }
    )
}
