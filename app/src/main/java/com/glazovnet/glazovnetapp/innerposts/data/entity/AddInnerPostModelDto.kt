package com.glazovnet.glazovnetapp.innerposts.data.entity

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Serializable
@Keep
data class AddInnerPostModelDto(
    val title: String?,
    val text: String
)
