package com.glazovnet.glazovnetapp.tariffs.data.entity

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class TariffModelDto(
    val id: String = "",
    val name: String,
    val description: String? = null,
    val categoryCode: Int,
    val maxSpeed: Int,
    val costPerMonth: Int,
    val prepaidTraffic: Int?,
    val prepaidTrafficDescription: String?
)
