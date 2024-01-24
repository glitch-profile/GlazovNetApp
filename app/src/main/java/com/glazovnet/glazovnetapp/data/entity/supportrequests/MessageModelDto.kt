package com.glazovnet.glazovnetapp.data.entity.supportrequests

import kotlinx.serialization.Serializable

@Serializable
data class MessageModelDto(
    val id: String,
    val senderId: String,
    val senderName: String,
    val text: String,
    val timestamp: Long
)
