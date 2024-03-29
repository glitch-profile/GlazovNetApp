package com.glazovnet.glazovnetapp.core.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun CheckedButton(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    isChecked: Boolean,
    onStateChanges: (status: Boolean) -> Unit,
    titleMinLines: Int = 1,
    titleMaxLines: Int = 2,
    descriptionMinLines: Int = 1,
    descriptionMaxLines: Int = 2,
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isChecked) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surfaceVariant,
        label = "checkedButtonBackgroundColor"
    )
    val titleColor by animateColorAsState(
        targetValue = if (isChecked) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurface,
        label = "checkedButtonTitleColor"
    )
    val descriptionColor by animateColorAsState(
        targetValue = if (isChecked) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "checkedButtonDescriptionColor"
    )
    Column(
        modifier = modifier
//            .clip(MaterialTheme.shapes.small)
            .background(backgroundColor)
            .clickable { onStateChanges.invoke(!isChecked) }
            .padding(8.dp)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = title,
            overflow = TextOverflow.Ellipsis,
            minLines = titleMinLines,
            maxLines = titleMaxLines,
            style = MaterialTheme.typography.titleMedium,
            color = titleColor
        )
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = description,
            overflow = TextOverflow.Ellipsis,
            minLines = descriptionMinLines,
            maxLines = descriptionMaxLines,
            style = MaterialTheme.typography.bodyMedium,
            color = descriptionColor
        )
    }
}