package com.glazovnet.glazovnetapp.supportrequests.data.entity

import kotlinx.serialization.Serializable

@Serializable
data class MessageModelDto(
    val id: String,
    val senderId: String,
    val senderName: String,
    val text: String,
    val timestamp: Long
)
