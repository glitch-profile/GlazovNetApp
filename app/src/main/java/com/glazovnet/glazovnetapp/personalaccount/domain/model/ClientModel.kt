package com.glazovnet.glazovnetapp.personalaccount.domain.model

import java.time.OffsetDateTime

data class ClientModel(
    val accountNumber: String,
    val tariffId: String?,
    val address: String,
    val balance: Float,
    val accountCreationDate: OffsetDateTime,
    val debitDate: OffsetDateTime,
    val isAccountActive: Boolean,
    val connectedServices: List<String>
)