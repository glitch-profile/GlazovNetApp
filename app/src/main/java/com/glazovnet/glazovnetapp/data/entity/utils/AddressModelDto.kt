package com.glazovnet.glazovnetapp.data.entity.utils

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class AddressModelDto(
    val city: String,
    val street: String,
    val houseNumbers: List<String>
)
