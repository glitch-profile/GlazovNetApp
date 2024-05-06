package com.glazovnet.glazovnetapp.services.data.entity

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class ServiceModelDto(
    val id: String,
    val name: String,
    val nameEn: String,
    val description: String,
    val descriptionEn: String,
    val costPerMonth: Int,
    val connectionCost: Int?,
    val isActive: Boolean
)
