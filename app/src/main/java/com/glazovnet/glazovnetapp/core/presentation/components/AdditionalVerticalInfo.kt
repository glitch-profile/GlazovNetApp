package com.glazovnet.glazovnetapp.core.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun AdditionalVerticalInfo(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    titleMaxLines: Int = Int.MAX_VALUE,
    descriptionMaxLines: Int = 1
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = title,
            overflow = TextOverflow.Ellipsis,
            maxLines = titleMaxLines,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = description,
            overflow = TextOverflow.Ellipsis,
            maxLines = descriptionMaxLines,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}