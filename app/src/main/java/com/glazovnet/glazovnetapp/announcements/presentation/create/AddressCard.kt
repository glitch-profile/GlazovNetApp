package com.glazovnet.glazovnetapp.announcements.presentation.create

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import com.glazovnet.glazovnetapp.announcements.domain.model.AddressFilterElement

@Composable
fun AddressCard(
    modifier: Modifier = Modifier,
    address: AddressFilterElement,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        if (address.isSelected) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.background,
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
            text = "${address.city}, ${address.street}, ${address.houseNumber}",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}