package com.glazovnet.glazovnetapp.tariffs.data.repositoryimpl

import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.core.data.utils.ApiResponseDto
import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.tariffs.data.entity.TariffModelDto
import com.glazovnet.glazovnetapp.tariffs.data.mappers.toTariffModel
import com.glazovnet.glazovnetapp.tariffs.domain.model.TariffModel
import com.glazovnet.glazovnetapp.tariffs.domain.repository.TariffsApiRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.put
import javax.inject.Inject
import javax.inject.Named

private const val PATH = "api/tariffs"

class TariffsApiRepositoryImpl @Inject constructor(
    @Named("RestClient") private val client: HttpClient
): TariffsApiRepository {

    override suspend fun getAllTariffs(
        token: String,
        clientId: String?
    ): Resource<List<TariffModel>> {
        return try {
            val response: ApiResponseDto<List<TariffModelDto>> = client.get(PATH){
                header("client_id", clientId)
                bearerAuth(token)
            }.body()
            if (response.status) {
                Resource.Success(
                    data = response.data.map { it.toTariffModel() }
                )
            } else {
                Resource.Error(
                    stringResourceId = R.string.api_response_server_error,
                    message = response.message
                )
            }
        } catch (e: Exception) {
            Resource.generateFromApiResponseError(e)
        }
    }

    override suspend fun getActiveTariffs(
        token: String,
        clientId: String?
    ): Resource<List<TariffModel>> {
        return try {
            val response: ApiResponseDto<List<TariffModelDto>> = client.get("$PATH/active"){
                header("client_id", clientId)
                bearerAuth(token)
            }.body()
            if (response.status) {
                Resource.Success(
                    data = response.data.map { it.toTariffModel() }
                )
            } else {
                Resource.Error(
                    stringResourceId = R.string.api_response_server_error,
                    message = response.message
                )
            }
        } catch (e: Exception) {
            Resource.generateFromApiResponseError(e)
        }
    }

    override suspend fun getArchiveTariffs(token: String, clientId: String?): Resource<List<TariffModel>> {
        return try {
            val response: ApiResponseDto<List<TariffModelDto>> = client.get("$PATH/archive"){
                header("client_id", clientId)
                bearerAuth(token)
            }.body()
            if (response.status) {
                Resource.Success(
                    data = response.data.map { it.toTariffModel() }
                )
            } else {
                Resource.Error(
                    stringResourceId = R.string.api_response_server_error,
                    message = response.message
                )
            }
        } catch (e: Exception) {
            Resource.generateFromApiResponseError(e)
        }
    }

    override suspend fun getTariffById(tariffId: String, token: String): Resource<TariffModel?> {
        return try {
            val response: ApiResponseDto<TariffModelDto?> = client.get("$PATH/$tariffId") {
                bearerAuth(token)
            }.body()
            if (response.status) {
                Resource.Success(
                    data = response.data!!.toTariffModel()
                )
            } else {
                Resource.Error(
                    stringResourceId = R.string.api_response_server_error,
                    message = response.message
                )
            }
        } catch (e: Exception) {
            Resource.generateFromApiResponseError(e)
        }
    }

    override suspend fun changeTariff(
        token: String,
        clientId: String,
        newTariffId: String?
    ): Resource<Unit> {
        return try {
            val response: ApiResponseDto<Unit> = client.put("$PATH/update-tariff-for-client") {
                bearerAuth(token)
                header("client_id", clientId)
                header("tariff_id", newTariffId)
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