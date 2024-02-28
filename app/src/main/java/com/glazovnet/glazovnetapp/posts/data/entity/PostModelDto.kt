package com.glazovnet.glazovnetapp.posts.data.entity

import androidx.annotation.Keep
import com.glazovnet.glazovnetapp.core.data.utils.ImageModelDto
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
