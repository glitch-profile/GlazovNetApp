package com.glazovnet.glazovnetapp.tariffs.domain.model

data class TariffModel(
    val id: String,
    val name: String,
    val description: String?,
    val category: TariffType,
    val maxSpeed: Int, // megabits/second
    val costPerMonth: Int,
    val prepaidTraffic: Long?, // gigabytes
    val prepaidTrafficDescription: String?,
    val isForOrganization: Boolean
)