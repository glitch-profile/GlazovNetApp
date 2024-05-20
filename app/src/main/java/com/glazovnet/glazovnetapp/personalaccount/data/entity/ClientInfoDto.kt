package com.glazovnet.glazovnetapp.personalaccount.data.entity

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Serializable
@Keep
data class ClientInfoDto(
    val accountNumber: String,
    val connectedOrganizationName: String?,
    val address: ClientAddressDto,
    val tariffId: String,
    val pendingTariffId: String?,
    val balance: Float,
    val accountCreationDate: Long,
    val debitDate: Long,
    val isAccountActive: Boolean,
    val connectedServices: List<String>
)
