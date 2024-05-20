package com.glazovnet.glazovnetapp.personalaccount.domain.model

import java.time.OffsetDateTime

data class ClientModel(
    val accountNumber: String,
    val connectedOrganizationName: String?,
    val tariffId: String,
    val pendingTariffId: String?,
    val address: String,
    val balance: Float,
    val accountCreationDate: OffsetDateTime,
    val debitDate: OffsetDateTime,
    val isAccountActive: Boolean,
    val connectedServices: List<String>
)