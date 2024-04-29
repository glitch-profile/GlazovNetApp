package com.glazovnet.glazovnetapp.supportrequests.data.entity

import kotlinx.serialization.Serializable

@Serializable
data class SupportRequestDto(
    val id: String,
    val creatorPersonId: String,
    val creatorClientId: String,
    val creatorName: String,
    val associatedSupportId: String?,
    val title: String,
    val description: String,
    val creationDate: Long,
    val reopenDate: Long?,
    val isNotificationsEnabled: Boolean,
    val status: Int
)
