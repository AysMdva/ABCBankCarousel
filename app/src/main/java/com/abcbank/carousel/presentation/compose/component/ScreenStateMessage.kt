package com.abcbank.carousel.presentation.compose.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.abcbank.carousel.R

@Composable
fun ScreenStateMessage(
    title: String,
    message: String,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val contentPadding = dimensionResource(R.dimen.screen_state_content_padding)
    val itemSpacing = dimensionResource(R.dimen.spacing_medium)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(contentPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(itemSpacing)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (actionLabel != null && onActionClick != null) {
            Button(onClick = onActionClick) {
                Text(text = actionLabel)
            }
        }
    }
}
