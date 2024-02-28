package com.glazovnet.glazovnetapp.announcements.data.entity

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class AddressModelDto(
    val city: String,
    val street: String,
    val houseNumbers: List<String>
)
