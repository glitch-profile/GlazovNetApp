package com.glazovnet.glazovnetapp.settings.notifications.data.dto

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class NotificationTopicDto(
    val topicCode: String,
    val name: String,
    val nameEn: String?,
    val description: String,
    val descriptionEn: String?
)
