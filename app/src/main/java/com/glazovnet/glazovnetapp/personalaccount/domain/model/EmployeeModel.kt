package com.glazovnet.glazovnetapp.personalaccount.domain.model

import java.time.OffsetDateTime

data class EmployeeModel(
    val accountCreationDate: OffsetDateTime,
    val averageRating: Float,
    val numberOfRatings: Int,
    val roles: List<String>,
)
