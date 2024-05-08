package com.glazovnet.glazovnetapp.tariffs.data.entity

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class TariffModelDto(
    val id: String = "",
    val name: String,
    val maxSpeed: Int, // kilobits/s
    val costPerMonth: Int,
    val prepaidTraffic: Long?, // kilobytes
    val prepaidTrafficDescription: String?,
    val isActive: Boolean,
    val isForOrganization: Boolean
)
