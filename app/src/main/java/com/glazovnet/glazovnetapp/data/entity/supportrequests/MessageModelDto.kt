package com.glazovnet.glazovnetapp.data.entity.supportrequests

import com.glazovnet.glazovnetapp.domain.models.supportrequest.MessageModel
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId

@Serializable
data class MessageModelDto(
    val id: String,
    val senderId: String,
    val senderName: String,
    val text: String,
    val timestamp: Long
) {
    fun toMessageModel(): MessageModel {
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
}
