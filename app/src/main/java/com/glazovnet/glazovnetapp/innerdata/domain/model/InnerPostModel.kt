package com.glazovnet.glazovnetapp.innerdata.domain.model

import java.time.OffsetDateTime

data class InnerPostModel(
    val id: String,
    val title: String?,
    val text: String,
    val creationDate: OffsetDateTime
)
