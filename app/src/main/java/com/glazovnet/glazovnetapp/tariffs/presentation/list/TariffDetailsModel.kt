package com.glazovnet.glazovnetapp.tariffs.presentation.list

import com.glazovnet.glazovnetapp.tariffs.domain.model.TariffModel

data class TariffDetailsModel(
    val tariff: TariffModel,
    val isCurrentTariff: Boolean,
    val isPendingTariff: Boolean
)
