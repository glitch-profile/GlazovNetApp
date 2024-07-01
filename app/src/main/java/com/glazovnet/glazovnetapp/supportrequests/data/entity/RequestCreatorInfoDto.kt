package com.glazovnet.glazovnetapp.supportrequests.data.entity

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Serializable
@Keep
data class RequestCreatorInfoDto(
    val accountNumber: String,
    val fullName: String
)
