package com.glazovnet.glazovnetapp.announcements.domain.model

data class AddressFilterElement(
    val city: String,
    val street: String,
    val houseNumber: String,
    val isSelected: Boolean = false
)
