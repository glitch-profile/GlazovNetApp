package com.glazovnet.glazovnetapp.data.entity.supportrequests

import com.glazovnet.glazovnetapp.domain.models.supportrequest.RequestsStatus
import com.glazovnet.glazovnetapp.domain.models.supportrequest.SupportRequestModel
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId

@Serializable
data class SupportRequestDto(
    val id: String,
    val creatorId: String,
    val associatedSupportId: String?,
    val title: String,
    val description: String,
    val messages: List<MessageModelDto> = emptyList(),
    val creationDate: Long,
    val status: Int
)
