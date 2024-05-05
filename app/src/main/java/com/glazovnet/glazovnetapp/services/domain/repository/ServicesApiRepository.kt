package com.glazovnet.glazovnetapp.services.domain.repository

import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.services.domain.model.ServiceModel

interface ServicesApiRepository {

    suspend fun getAllServices(token: String): Resource<List<ServiceModel>>

    suspend fun getConnectedServices(token: String, clientId: String): Resource<List<ServiceModel>>

    suspend fun getConnectedServicesIds(token: String, clientId: String): Resource<List<String>>

    suspend fun connectService(token: String, clientId: String, serviceId: String): Resource<Unit>

    suspend fun disconnectService(token: String, clientId: String, serviceId: String): Resource<Unit>

}