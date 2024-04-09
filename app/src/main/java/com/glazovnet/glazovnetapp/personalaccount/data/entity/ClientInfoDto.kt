package com.glazovnet.glazovnetapp.personalaccount.data.entity

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Serializable
@Keep
data class ClientInfoDto(
    val accountNumber: String,
    val login: String,
    val password: String,
    val isNotificationsEnabled: Boolean?,
    val profileAvatar: String?,
    val firstName: String,
    val lastName: String,
    val middleName: String?,
    val tariffId: String?,
    val address: ClientAddressDto,
    val balance: Double,
    val accountCreationDate: String,
    val debitDate: String,
    val isAccountActive: Boolean,
    val connectedServices: List<String> = emptyList()
)
