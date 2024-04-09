package com.glazovnet.glazovnetapp.personalaccount.data.mapper

import com.glazovnet.glazovnetapp.personalaccount.data.entity.ClientInfoDto
import com.glazovnet.glazovnetapp.personalaccount.domain.model.ClientInfo
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

fun ClientInfoDto.toClientInfo(): ClientInfo {
    val addressString = "${this.address.cityName.replaceFirstChar { it.uppercaseChar() }}, " +
            "${this.address.streetName.replaceFirstChar { it.uppercaseChar() }}, " +
            "${this.address.houseNumber}-${this.address.roomNumber}"
    val parsedAccountCreationDate = OffsetDateTime.parse(this.accountCreationDate, DateTimeFormatter.ISO_DATE)
    val parsedDebitDate = OffsetDateTime.parse(this.debitDate, DateTimeFormatter.ISO_DATE)

    return ClientInfo(
        accountNumber = this.accountNumber,
        login = this.login,
        password = this.password,
        isNotificationsEnabled = this.isNotificationsEnabled ?: false,
        profileImageUrl = this.profileAvatar,
        firstName = this.firstName,
        lastName = this.lastName,
        middleName = this.middleName,
        tariffId = this.tariffId,
        address = addressString,
        balance = this.balance,
        accountCreationDate = parsedAccountCreationDate,
        debitDate = parsedDebitDate,
        isAccountActive = this.isAccountActive,
        connectedServices = this.connectedServices
    )
}