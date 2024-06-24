package com.glazovnet.glazovnetapp.personalaccount.data.entity

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Serializable
@Keep
data class TransactionModelDto(
    val id: String,
    val transactionTimestamp: Long,
    val amount: Float,
    val isIncoming: Boolean,
    val note: String?
)