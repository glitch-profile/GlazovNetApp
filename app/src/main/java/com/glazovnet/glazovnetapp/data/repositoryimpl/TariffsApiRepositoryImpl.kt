package com.glazovnet.glazovnetapp.data.repositoryimpl

import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.data.entity.tariffs.TariffModelDto
import com.glazovnet.glazovnetapp.data.entity.utils.ApiResponseDto
import com.glazovnet.glazovnetapp.data.mappers.toTariffModel
import com.glazovnet.glazovnetapp.domain.models.tariffs.TariffModel
import com.glazovnet.glazovnetapp.domain.repository.TariffsApiRepository
import com.glazovnet.glazovnetapp.domain.utils.Resource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import javax.inject.Inject
import javax.inject.Named

private const val PATH = "api/tariffs"

class TariffsApiRepositoryImpl @Inject constructor(
    @Named("RestClient") private val client: HttpClient
): TariffsApiRepository {

    override suspend fun getAllTariffs(token: String): Resource<List<TariffModel>> {
        return try {
            val response: ApiResponseDto<List<TariffModelDto>> = client.get("$PATH/"){
                bearerAuth(token)
            }.body()
            if (response.status) {
                Resource.Success(
                    data = response.data.map{ it.toTariffModel() }
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
}