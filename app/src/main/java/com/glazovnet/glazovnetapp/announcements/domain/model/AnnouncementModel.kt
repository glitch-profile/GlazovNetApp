package com.glazovnet.glazovnetapp.announcements.domain.model

import java.time.OffsetDateTime

data class AnnouncementModel(
    val id: String,
    val addresses: List<AddressFilterElement>,
    val title: String,
    val text: String,
    val creationDate: OffsetDateTime? = null
)
