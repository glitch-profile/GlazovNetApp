package com.glazovnet.glazovnetapp.domain.models.supportrequest

import java.time.OffsetDateTime

data class SupportRequestModel(
    val id: String = "",
    val creatorId: String,
    val associatedSupportId: String? = null,
    val title: String,
    val description: String,
    val creationDate: OffsetDateTime? = null,
    val status: RequestsStatus = RequestsStatus.Active
)
