package com.glazovnet.glazovnetapp.announcements.presentation.create

import com.glazovnet.glazovnetapp.announcements.domain.model.AddressFilterElement

data class AddressState(
    val address: AddressFilterElement,
    val isSelected: Boolean
)
