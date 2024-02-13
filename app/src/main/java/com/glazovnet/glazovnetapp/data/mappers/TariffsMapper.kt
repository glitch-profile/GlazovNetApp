package com.glazovnet.glazovnetapp.data.mappers

import com.glazovnet.glazovnetapp.data.entity.tariffs.TariffModelDto
import com.glazovnet.glazovnetapp.domain.models.tariffs.TariffModel
import com.glazovnet.glazovnetapp.domain.models.tariffs.TariffType
import com.glazovnet.glazovnetapp.domain.models.tariffs.TariffType.Companion.toTariffTypeCode

fun TariffModelDto.toTariffModel(): TariffModel {
    return TariffModel(
        id = id,
        name = name,
        description = description,
        category = TariffType.fromTariffTypeCode(this.categoryCode),
        maxSpeed = maxSpeed,
        costPerMonth = costPerMonth
    )
}

fun TariffModel.toTariffModelDto(): TariffModelDto {
    return TariffModelDto(
        id = id,
        name = name,
        categoryCode = this.category.toTariffTypeCode(),
        description = description,
        maxSpeed = maxSpeed,
        costPerMonth = costPerMonth
    )
}