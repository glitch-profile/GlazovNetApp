package com.glazovnet.glazovnetapp.personalaccount.data.mapper

import com.glazovnet.glazovnetapp.personalaccount.data.entity.ClientInfoDto
import com.glazovnet.glazovnetapp.personalaccount.domain.model.ClientModel
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId

fun ClientInfoDto.toClientModel(): ClientModel {
    val addressString = "${this.address.cityName.replaceFirstChar { it.uppercaseChar() }}, " +
            "${this.address.streetName.replaceFirstChar { it.uppercaseChar() }}, " +
            "${this.address.houseNumber}-${this.address.roomNumber}"
    val parsedAccountCreationDate = OffsetDateTime.ofInstant(
        Instant.ofEpochSecond(this.accountCreationDate),
        ZoneId.systemDefault()
    )
    val parsedDebitDate = OffsetDateTime.ofInstant(
        Instant.ofEpochSecond(this.debitDate),
        ZoneId.systemDefault()
    )

    return ClientModel(
        accountNumber = this.accountNumber,
        connectedOrganizationName = this.connectedOrganizationName,
        tariffId = this.tariffId,
        pendingTariffId = this.pendingTariffId,
        address = addressString,
        balance = this.balance,
        accountCreationDate = parsedAccountCreationDate,
        debitDate = parsedDebitDate,
        isAccountActive = this.isAccountActive,
        connectedServices = this.connectedServices
    )
}