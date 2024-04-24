package com.glazovnet.glazovnetapp.personalaccount.data.entity

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Serializable
@Keep
data class EmployeeInfoDto(
    val accountCreationDate: Long,
    val roles: List<String>,
    val overallRating: Int,
    val numberOfRatings: Int
)
