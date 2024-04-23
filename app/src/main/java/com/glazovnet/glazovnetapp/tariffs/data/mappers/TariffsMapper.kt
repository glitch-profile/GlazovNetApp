package com.glazovnet.glazovnetapp.tariffs.data.mappers

import com.glazovnet.glazovnetapp.tariffs.data.entity.TariffModelDto
import com.glazovnet.glazovnetapp.tariffs.domain.model.TariffModel
import com.glazovnet.glazovnetapp.tariffs.domain.model.TariffType

fun TariffModelDto.toTariffModel(): TariffModel {
    return TariffModel(
        id = id,
        name = name,
        description = description,
        category = TariffType.fromTariffTypeCode(this.categoryCode),
        maxSpeed = maxSpeed / 1024,
        costPerMonth = costPerMonth,
        prepaidTraffic = this.prepaidTraffic?.div(1024),
        prepaidTrafficDescription = this.prepaidTrafficDescription,
        isForOrganization = this.isForOrganization
    )
}