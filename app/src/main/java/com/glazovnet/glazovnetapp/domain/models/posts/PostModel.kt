package com.glazovnet.glazovnetapp.domain.models.posts

import com.glazovnet.glazovnetapp.data.entity.utils.ImageModelDto
import java.time.OffsetDateTime

data class PostModel(
    val id: String = "",
    val title: String,
    val creationDateTime: OffsetDateTime? = null,
    val lastEditDate: OffsetDateTime? = null,
    val text: String,
    val image: ImageModelDto? = null,
)
