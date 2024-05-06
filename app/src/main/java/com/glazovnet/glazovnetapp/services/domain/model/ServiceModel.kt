package com.glazovnet.glazovnetapp.services.domain.model

data class ServiceModel(
    val id: String,
    val name: String,
    val description: String,
    val costPerMonth: Int,
    val connectionCost: Int?,
    val isActive: Boolean
)
