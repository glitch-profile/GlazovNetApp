package com.glazovnet.glazovnetapp.domain.models.announcements

data class AddressFilterElement(
    val city: String,
    val street: String,
    val houseNumber: String,
    val isSelected: Boolean = false
)
