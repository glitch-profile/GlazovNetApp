package com.glazovnet.glazovnetapp.tariffs.domain.model

data class TariffModel(
    val id: String,
    val name: String,
    val description: String?,
    val maxSpeed: Int, // kilobits/second
    val costPerMonth: Int,
    val prepaidTraffic: Long?, // gigabytes
    val prepaidTrafficDescription: String?,
    val isActive: Boolean,
    val isForOrganization: Boolean
)