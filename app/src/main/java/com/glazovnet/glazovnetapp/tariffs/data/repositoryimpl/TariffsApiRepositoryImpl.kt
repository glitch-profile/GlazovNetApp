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
import javax.inject.Inject
import javax.inject.Named

private const val PATH = "api/tariffs"
private const val INNER_TARIFFS_PATH = "api/inner/tariffs"

class TariffsApiRepositoryImpl @Inject constructor(
    @Named("RestClient") private val client: HttpClient
): TariffsApiRepository {

    override suspend fun getAllTariffs(token: String): Resource<List<TariffModel>> {
        return try {
//            val response: ApiResponseDto<List<TariffModelDto>> = client.get("$PATH/"){
            val response: ApiResponseDto<List<TariffModelDto>> = client.get(INNER_TARIFFS_PATH){
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
            val response: ApiResponseDto<TariffModelDto> = client.get("$PATH/$tariffId") {
                bearerAuth(token)
            }.body()
            if (response.status) {
                Resource.Success(
                    data = response.data.toTariffModel()
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