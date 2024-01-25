package com.glazovnet.glazovnetapp.data.mappers

import com.glazovnet.glazovnetapp.data.entity.supportrequests.SupportRequestDto
import com.glazovnet.glazovnetapp.domain.models.supportrequest.RequestsStatus
import com.glazovnet.glazovnetapp.domain.models.supportrequest.RequestsStatus.Companion.convertToIntCode
import com.glazovnet.glazovnetapp.domain.models.supportrequest.SupportRequestModel
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId

fun SupportRequestModel.toSupportRequestDto(): SupportRequestDto {
    val convertedCreationDate = this.creationDate?.toEpochSecond() ?: 0L
    return SupportRequestDto(
        id = this.id,
        creatorId = this.creatorId,
        creatorName = this.creatorName,
        associatedSupportId = this.associatedSupportId,
        title = this.title,
        description = this.description,
        creationDate = convertedCreationDate,
        status = this.status.convertToIntCode()
    )
}

fun SupportRequestDto.toSupportRequestModel(): SupportRequestModel {
    val convertedCreationDate = OffsetDateTime.ofInstant(
        Instant.ofEpochSecond(this.creationDate),
        ZoneId.systemDefault()
    )
    return SupportRequestModel(
        id = this.id,
        creatorId = this.creatorId,
        creatorName = this.creatorName,
        associatedSupportId = this.associatedSupportId,
        title = this.title,
        description = this.description,
        creationDate = convertedCreationDate,
        status = RequestsStatus.getFromIntCode(this.status)
    )
}