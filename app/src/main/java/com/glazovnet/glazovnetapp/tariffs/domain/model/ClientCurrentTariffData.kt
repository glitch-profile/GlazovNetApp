package com.glazovnet.glazovnetapp.tariffs.domain.model

import java.time.OffsetDateTime

data class ClientCurrentTariffData(
    val currentTariff: TariffModel,
    val pendingTariff: TariffModel?,
    val billingDate: OffsetDateTime
)
