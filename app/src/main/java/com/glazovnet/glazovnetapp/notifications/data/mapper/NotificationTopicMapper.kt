package com.glazovnet.glazovnetapp.notifications.data.mapper

import com.glazovnet.glazovnetapp.notifications.data.dto.NotificationTopicDto
import com.glazovnet.glazovnetapp.notifications.domain.model.NotificationTopicModel

fun NotificationTopicDto.toNotificationTopicModel(): NotificationTopicModel {
    return NotificationTopicModel(
        topicCode = this.topicCode,
        name = this.name,
        description = this.description
    )
}