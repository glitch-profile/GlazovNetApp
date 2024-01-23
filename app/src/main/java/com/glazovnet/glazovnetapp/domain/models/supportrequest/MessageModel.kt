package com.glazovnet.glazovnetapp.domain.models.supportrequest

import java.time.OffsetDateTime

data class MessageModel(
    val id: String = "",
    val senderId: String,
    val senderName: String,
    val text: String,
    val timestamp: OffsetDateTime?,
    val isOwnMessage: Boolean = false
)
