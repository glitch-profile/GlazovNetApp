package com.glazovnet.glazovnetapp.personalaccount.data.mapper

import com.glazovnet.glazovnetapp.personalaccount.data.entity.EmployeeInfoDto
import com.glazovnet.glazovnetapp.personalaccount.domain.model.EmployeeModel
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId

fun EmployeeInfoDto.toEmployeeModel(): EmployeeModel {
    val creationDate = OffsetDateTime.ofInstant(
        Instant.ofEpochSecond(this.accountCreationDate),
        ZoneId.systemDefault()
    )
    val averageRating = if (this.numberOfRatings == 0 || this.overallRating == 0) 0.0f
    else this.overallRating.toFloat() / this.numberOfRatings
    return EmployeeModel(
        accountCreationDate = creationDate,
        averageRating = averageRating,
        roles = this.roles
    )
}
