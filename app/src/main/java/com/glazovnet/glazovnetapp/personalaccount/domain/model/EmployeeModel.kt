package com.glazovnet.glazovnetapp.personalaccount.domain.model

import java.time.OffsetDateTime

data class EmployeeModel(
    val accountCreationDate: OffsetDateTime,
    val averageRating: Float,
    val roles: List<String>,
)
