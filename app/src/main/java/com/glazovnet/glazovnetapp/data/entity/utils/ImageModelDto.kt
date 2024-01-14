package com.glazovnet.glazovnetapp.data.entity.utils

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Serializable
@Keep
data class ImageModelDto(
    val imageUrl: String,
    val imageWidth: Int,
    val imageHeight: Int
)
