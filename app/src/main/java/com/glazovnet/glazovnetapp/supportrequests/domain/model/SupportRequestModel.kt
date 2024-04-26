package com.glazovnet.glazovnetapp.supportrequests.domain.model

import java.time.OffsetDateTime

data class SupportRequestModel(
    val id: String,
    val creatorPersonId: String,
    val creatorClientId: String,
    val creatorName: String,
    val associatedSupportId: String?,
    val title: String,
    val description: String,
    val creationDate: OffsetDateTime,
    val reopenDate: OffsetDateTime?,
    val isNotificationsEnabled: Boolean,
    val status: RequestStatus = RequestStatus.NotReviewed
)
