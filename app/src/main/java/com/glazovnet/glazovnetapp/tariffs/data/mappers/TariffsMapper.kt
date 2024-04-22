package com.glazovnet.glazovnetapp.tariffs.data.mappers

import com.glazovnet.glazovnetapp.tariffs.data.entity.TariffModelDto
import com.glazovnet.glazovnetapp.tariffs.domain.model.TariffModel
import com.glazovnet.glazovnetapp.tariffs.domain.model.TariffType
import com.glazovnet.glazovnetapp.tariffs.domain.model.TariffType.Companion.toTariffTypeCode

fun TariffModelDto.toTariffModel(): TariffModel {
    return TariffModel(
        id = id,
        name = name,
        description = description,
        category = TariffType.fromTariffTypeCode(this.categoryCode),
        maxSpeed = maxSpeed,
        costPerMonth = costPerMonth,
        prepaidTraffic = this.prepaidTraffic,
        prepaidTrafficDescription = this.prepaidTrafficDescription,
        isForOrganisation = this.isForOrganisation
    )
}

fun TariffModel.toTariffModelDto(): TariffModelDto {
    return TariffModelDto(
        id = id,
        name = name,
        categoryCode = this.category.toTariffTypeCode(),
        description = description,
        maxSpeed = maxSpeed,
        costPerMonth = costPerMonth,
        prepaidTraffic = this.prepaidTraffic,
        prepaidTrafficDescription = this.prepaidTrafficDescription,
        isForOrganisation = this.isForOrganisation
    )
}