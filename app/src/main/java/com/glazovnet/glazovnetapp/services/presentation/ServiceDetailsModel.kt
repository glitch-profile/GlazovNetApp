package com.glazovnet.glazovnetapp.services.presentation

import com.glazovnet.glazovnetapp.services.domain.model.ServiceModel

data class ServiceDetailsModel(
    val service: ServiceModel,
    val isServiceConnected: Boolean,
)
