package com.glazovnet.glazovnetapp.personalaccount.data.entity

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class AddFundsModelDto(
    val amount: Float,
    val note: String?
)