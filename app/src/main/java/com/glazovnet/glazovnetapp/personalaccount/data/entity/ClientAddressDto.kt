package com.glazovnet.glazovnetapp.personalaccount.data.entity

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Serializable
@Keep
data class ClientAddressDto(
    val cityName: String,
    val streetName: String,
    val houseNumber: String,
    val roomNumber: Int
)
