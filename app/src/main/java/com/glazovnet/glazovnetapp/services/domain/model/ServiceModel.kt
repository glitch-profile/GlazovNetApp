package com.glazovnet.glazovnetapp.services.domain.model

data class ServiceModel(
    val id: String,
    val name: String,
    val description: String,
    val cost: Float,
    val isActive: Boolean
)
