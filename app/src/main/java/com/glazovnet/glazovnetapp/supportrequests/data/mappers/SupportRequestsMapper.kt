package com.glazovnet.glazovnetapp.supportrequests.data.mappers

import com.glazovnet.glazovnetapp.supportrequests.data.entity.SupportRequestDto
import com.glazovnet.glazovnetapp.supportrequests.domain.model.RequestStatus
import com.glazovnet.glazovnetapp.supportrequests.domain.model.SupportRequestModel
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId

fun SupportRequestDto.toSupportRequestModel(): SupportRequestModel {
    val convertedCreationDate = OffsetDateTime.ofInstant(
        Instant.ofEpochSecond(this.creationDate),
        ZoneId.systemDefault()
    )
    val convertedReopenDate = if (this.reopenDate != null) {
        OffsetDateTime.ofInstant(
            Instant.ofEpochSecond(this.reopenDate),
            ZoneId.systemDefault()
        )
    } else null
    return SupportRequestModel(
        id = this.id,
        creatorPersonId = this.creatorPersonId,
        creatorClientId = this.creatorClientId,
        associatedSupportId = this.associatedSupportId,
        title = this.title,
        description = this.description,
        creationDate = convertedCreationDate,
        reopenDate = convertedReopenDate,
        isNotificationsEnabled = this.isNotificationsEnabled,
        status = RequestStatus.getFromIntCode(this.status)
    )
}