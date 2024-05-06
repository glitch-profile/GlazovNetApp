package com.glazovnet.glazovnetapp.services.data.mapper

import com.glazovnet.glazovnetapp.services.data.entity.ServiceModelDto
import com.glazovnet.glazovnetapp.services.domain.model.ServiceModel
import java.util.Locale

fun ServiceModelDto.toServiceModel(): ServiceModel {
    val name = if (Locale.getDefault().language == Locale("ru").language) this.name
    else this.nameEn
    val description = if (Locale.getDefault().language == Locale("ru").language) this.description
    else this.descriptionEn
    return ServiceModel(
        id = this.id,
        name = name,
        description = description,
        costPerMonth = this.costPerMonth,
        connectionCost = this.connectionCost,
        isActive = isActive
    )
}