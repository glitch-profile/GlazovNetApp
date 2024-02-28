package com.glazovnet.glazovnetapp.supportrequests.data.mappers

import com.glazovnet.glazovnetapp.supportrequests.data.entity.MessageModelDto
import com.glazovnet.glazovnetapp.supportrequests.domain.model.MessageModel
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