package com.glazovnet.glazovnetapp.supportrequests.data.mappers

import com.glazovnet.glazovnetapp.supportrequests.data.entity.SupportRequestDto
import com.glazovnet.glazovnetapp.supportrequests.domain.model.RequestStatus
import com.glazovnet.glazovnetapp.supportrequests.domain.model.RequestStatus.Companion.convertToIntCode
import com.glazovnet.glazovnetapp.supportrequests.domain.model.SupportRequestModel
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
        isNotificationsEnabled = this.isNotificationsEnabled,
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
        isNotificationsEnabled = this.isNotificationsEnabled,
        status = RequestStatus.getFromIntCode(this.status)
    )
}