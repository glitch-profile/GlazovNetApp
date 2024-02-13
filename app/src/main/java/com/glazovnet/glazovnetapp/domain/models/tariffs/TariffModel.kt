package com.glazovnet.glazovnetapp.domain.models.tariffs

data class TariffModel(
    val id: String,
    val name: String,
    val description: String? = null,
    val category: TariffType,
    val maxSpeed: Int,
    val costPerMonth: Int
)