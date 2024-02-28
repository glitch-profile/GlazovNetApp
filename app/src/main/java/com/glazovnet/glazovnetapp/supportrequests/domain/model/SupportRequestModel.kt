package com.glazovnet.glazovnetapp.supportrequests.domain.model

import java.time.OffsetDateTime

data class SupportRequestModel(
    val id: String = "",
    val creatorId: String,
    val creatorName: String = "",
    val associatedSupportId: String? = null,
    val title: String,
    val description: String,
    val creationDate: OffsetDateTime? = null,
    val isNotificationsEnabled: Boolean = true,
    val status: RequestStatus = RequestStatus.NotReviewed
)
