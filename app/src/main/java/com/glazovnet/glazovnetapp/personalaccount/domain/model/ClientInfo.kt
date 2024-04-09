package com.glazovnet.glazovnetapp.personalaccount.domain.model

import java.time.OffsetDateTime

data class ClientInfo(
    val accountNumber: String,
    val login: String,
    val password: String,
    val isNotificationsEnabled: Boolean,
    val profileImageUrl: String?,
    val firstName: String,
    val lastName: String,
    val middleName: String?,
    val tariffId: String?,
    val address: String,
    val balance: Double,
    val accountCreationDate: OffsetDateTime,
    val debitDate: OffsetDateTime,
    val isAccountActive: Boolean,
    val connectedServices: List<String>
)