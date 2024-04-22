package com.glazovnet.glazovnetapp.tariffs.domain.model

data class TariffModel(
    val id: String,
    val name: String,
    val description: String?,
    val category: TariffType,
    val maxSpeed: Int,
    val costPerMonth: Int,
    val prepaidTraffic: Int?,
    val prepaidTrafficDescription: String?,
    val isForOrganisation: Boolean
)