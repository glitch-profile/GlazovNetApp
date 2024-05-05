package com.glazovnet.glazovnetapp.services.data.repository

import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.core.data.utils.ApiResponseDto
import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.services.data.entity.ServiceModelDto
import com.glazovnet.glazovnetapp.services.data.mapper.toServiceModel
import com.glazovnet.glazovnetapp.services.domain.model.ServiceModel
import com.glazovnet.glazovnetapp.services.domain.repository.ServicesRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.header
import io.ktor.client.request.put
import javax.inject.Inject
import javax.inject.Named

private const val PATH = "/api/services"

class ServicesRepositoryImpl @Inject constructor(
    @Named("RestClient") private val client: HttpClient
): ServicesRepository {

    override suspend fun getAllServices(token: String): Resource<List<ServiceModel>> {
        return try {
            val response: ApiResponseDto<List<ServiceModelDto>> = client.put(PATH) {
                bearerAuth(token)
            }.body()
            if (response.status) {
                Resource.Success(
                    data = response.data.map { it.toServiceModel() }
                )
            } else Resource.Error(
                stringResourceId = R.string.api_response_server_error
            )
        } catch (e: Exception) {
            Resource.generateFromApiResponseError(e)
        }
    }

    override suspend fun getConnectedServices(
        token: String,
        clientId: String
    ): Resource<List<ServiceModel>> {
        return try {
            val response: ApiResponseDto<List<ServiceModelDto>> = client.put("$PATH/connected-for-client") {
                bearerAuth(token)
                header("client_id", clientId)
            }.body()
            if (response.status) {
                Resource.Success(
                    data = response.data.map { it.toServiceModel() }
                )
            } else Resource.Error(
                stringResourceId = R.string.api_response_server_error
            )
        } catch (e: Exception) {
            Resource.generateFromApiResponseError(e)
        }
    }

    override suspend fun getConnectedServicesIds(
        token: String,
        clientId: String
    ): Resource<List<String>> {
        return try {
            val response: ApiResponseDto<List<String>> = client.put("$PATH/connected-for-client/ids") {
                bearerAuth(token)
                header("client_id", clientId)
            }.body()
            if (response.status) {
                Resource.Success(
                    data = response.data
                )
            } else Resource.Error(
                stringResourceId = R.string.api_response_server_error
            )
        } catch (e: Exception) {
            Resource.generateFromApiResponseError(e)
        }
    }

    override suspend fun connectService(
        token: String,
        clientId: String,
        serviceId: String
    ): Resource<Unit> {
        return try {
            val response: ApiResponseDto<Unit> = client.put("$PATH/connect-client-service") {
                bearerAuth(token)
                header("client_id", clientId)
                header("service_id", serviceId)
            }.body()
            if (response.status) {
                Resource.Success(Unit)
            } else Resource.Error(
                stringResourceId = R.string.api_response_server_error
            )
        } catch (e: Exception) {
            Resource.generateFromApiResponseError(e)
        }
    }

    override suspend fun disconnectService(
        token: String,
        clientId: String,
        serviceId: String
    ): Resource<Unit> {
        return try {
            val response: ApiResponseDto<Unit> = client.put("$PATH/disconnect-client-service") {
                bearerAuth(token)
                header("client_id", clientId)
                header("service_id", serviceId)
            }.body()
            if (response.status) {
                Resource.Success(Unit)
            } else Resource.Error(
                stringResourceId = R.string.api_response_server_error
            )
        } catch (e: Exception) {
            Resource.generateFromApiResponseError(e)
        }
    }
}