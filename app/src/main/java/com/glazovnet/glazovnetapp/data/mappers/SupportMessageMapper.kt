package com.glazovnet.glazovnetapp.data.mappers

import com.glazovnet.glazovnetapp.data.entity.supportrequests.MessageModelDto
import com.glazovnet.glazovnetapp.domain.models.supportrequest.MessageModel
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId

fun MessageModelDto.toMessageModel(): MessageModel {
    val sendDateTime = OffsetDateTime.ofInstant(
        Instant.ofEpochSecond(this.timestamp),
        ZoneId.systemDefault()
    )
    return MessageModel(
        id = this.id,
        senderId = this.senderId,
        senderName = this.senderName,
        text = this.text,
        timestamp = sendDateTime
    )
}