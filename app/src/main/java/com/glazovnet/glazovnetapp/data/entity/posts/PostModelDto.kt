package com.glazovnet.glazovnetapp.data.entity.posts

import androidx.annotation.Keep
import com.glazovnet.glazovnetapp.data.entity.utils.ImageModelDto
import kotlinx.serialization.Serializable

@Serializable
@Keep
data class PostModelDto(
    val id: String,
    val title: String,
    val creationDate: Long,
    val lastEditDate: Long?,
    val text: String,
    val image: ImageModelDto?,
)
