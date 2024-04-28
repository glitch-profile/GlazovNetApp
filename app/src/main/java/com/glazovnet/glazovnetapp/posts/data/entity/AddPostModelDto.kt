package com.glazovnet.glazovnetapp.posts.data.entity

import androidx.annotation.Keep
import com.glazovnet.glazovnetapp.core.data.utils.ImageModelDto
import kotlinx.serialization.Serializable

@Serializable
@Keep
data class AddPostModelDto(
    val id: String?,
    val title: String,
    val text: String,
    val image: ImageModelDto?
)
