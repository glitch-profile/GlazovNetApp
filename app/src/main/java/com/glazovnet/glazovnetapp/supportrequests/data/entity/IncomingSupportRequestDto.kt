package com.glazovnet.glazovnetapp.supportrequests.data.entity

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Serializable
@Keep
data class IncomingSupportRequestDto(
    val creatorClientId: String,
    val title: String,
    val description: String,
    val isNotificationsEnabled: Boolean,
)
