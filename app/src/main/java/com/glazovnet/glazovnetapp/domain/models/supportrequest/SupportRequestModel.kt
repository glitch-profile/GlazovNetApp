package com.glazovnet.glazovnetapp.domain.models.supportrequest

import com.glazovnet.glazovnetapp.data.entity.supportrequests.SupportRequestDto
import com.glazovnet.glazovnetapp.domain.models.supportrequest.RequestsStatus.Companion.convertToIntCode
import java.time.OffsetDateTime

data class SupportRequestModel(
    val id: String = "",
    val creatorId: String,
    val associatedSupportId: String? = null,
    val title: String,
    val description: String,
    val creationDate: OffsetDateTime? = null,
    val status: RequestsStatus = RequestsStatus.Active
) {
    fun toSupportRequestDto(): SupportRequestDto {
        val convertedCreationDate = this.creationDate?.toEpochSecond() ?: 0L
        return SupportRequestDto(
            id = this.id,
            creatorId = this.creatorId,
            associatedSupportId = this.associatedSupportId,
            title = this.title,
            description = this.description,
            creationDate = convertedCreationDate,
            status = this.status.convertToIntCode()
        )
    }
}
