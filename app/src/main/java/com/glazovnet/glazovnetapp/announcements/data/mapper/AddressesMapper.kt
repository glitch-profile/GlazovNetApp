package com.glazovnet.glazovnetapp.announcements.data.mapper

import com.glazovnet.glazovnetapp.announcements.data.entity.AddressModelDto
import com.glazovnet.glazovnetapp.announcements.domain.model.AddressFilterElement

fun AddressModelDto.toAddressFilterElement(): List<AddressFilterElement> {
    val cityName = this.city.replaceFirstChar { it.uppercaseChar() }
    val streetName = this.street.replaceFirstChar { it.uppercaseChar() }
    val addresses = this.houseNumbers.map {
        AddressFilterElement(
            city = cityName,
            street = streetName,
            houseNumber = it
        )
    }
    return addresses
}