package com.glazovnet.glazovnetapp.domain.models.announcements

import java.time.OffsetDateTime

data class AnnouncementModel(
    val id: String,
    val addresses: List<AddressFilterElement>,
    val title: String,
    val text: String,
    val creationDate: OffsetDateTime? = null
)
