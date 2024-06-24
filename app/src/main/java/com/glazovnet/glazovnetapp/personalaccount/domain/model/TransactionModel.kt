package com.glazovnet.glazovnetapp.personalaccount.domain.model

import java.time.OffsetDateTime

data class TransactionModel(
    val id: String,
    val transactionTimestamp: OffsetDateTime,
    val amount: Float,
    val note: String?
)