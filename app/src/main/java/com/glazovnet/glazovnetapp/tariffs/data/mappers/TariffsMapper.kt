package com.glazovnet.glazovnetapp.tariffs.data.mappers

import com.glazovnet.glazovnetapp.tariffs.data.entity.TariffModelDto
import com.glazovnet.glazovnetapp.tariffs.domain.model.TariffModel

fun TariffModelDto.toTariffModel(): TariffModel {
    return TariffModel(
        id = id,
        name = name,
        description = description,
        maxSpeed = maxSpeed,
        costPerMonth = costPerMonth,
        prepaidTraffic = this.prepaidTraffic?.div(1024), //converting to megabytes
        prepaidTrafficDescription = this.prepaidTrafficDescription,
        isActive = this.isActive,
        isForOrganization = this.isForOrganization
    )
}