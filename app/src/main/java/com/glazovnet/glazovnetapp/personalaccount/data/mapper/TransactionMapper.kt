package com.glazovnet.glazovnetapp.personalaccount.data.mapper

import com.glazovnet.glazovnetapp.personalaccount.data.entity.TransactionModelDto
import com.glazovnet.glazovnetapp.personalaccount.domain.model.TransactionModel
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId

fun TransactionModelDto.toTransactionModel(): TransactionModel {
    val convertedTimestamp = OffsetDateTime.ofInstant(
        Instant.ofEpochSecond(this.transactionTimestamp),
        ZoneId.systemDefault()
    )
    return TransactionModel(
        id = this.id,
        transactionTimestamp = convertedTimestamp,
        amount = if (this.isIncoming) this.amount else this.amount * (-1),
        note = this.note
    )
}