package com.glazovnet.glazovnetapp.supportrequests.data.entity

import kotlinx.serialization.Serializable

@Serializable
data class SupportRequestDto(
    val id: String,
    val creatorId: String,
    val creatorName: String,
    val associatedSupportId: String?,
    val title: String,
    val description: String,
    val messages: List<MessageModelDto> = emptyList(),
    val creationDate: Long,
    val isNotificationsEnabled: Boolean,
    val status: Int
)
