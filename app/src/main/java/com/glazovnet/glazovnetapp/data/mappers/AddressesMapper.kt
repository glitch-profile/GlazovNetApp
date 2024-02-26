package com.glazovnet.glazovnetapp.data.mappers

import com.glazovnet.glazovnetapp.data.entity.utils.AddressModelDto
import com.glazovnet.glazovnetapp.domain.models.announcements.AddressFilterElement

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