package com.glazovnet.glazovnetapp.posts.domain.model

import com.glazovnet.glazovnetapp.core.data.utils.ImageModelDto
import java.time.OffsetDateTime

data class PostModel(
    val id: String = "",
    val title: String,
    val creationDateTime: OffsetDateTime? = null,
    val lastEditDate: OffsetDateTime? = null,
    val text: String,
    val image: ImageModelDto? = null,
)
