package com.glazovnet.glazovnetapp.announcements.presentation.create

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddressCard(
    modifier: Modifier = Modifier,
    addressState: AddressState,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        if (addressState.isSelected) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0f),
        label = "AddressRowBackgroundColor"
    )
    Row(
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxWidth()
            .clickable {
                onClick.invoke()
            }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = "${addressState.address.city}, ${addressState.address.street}, ${addressState.address.houseNumber}",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}