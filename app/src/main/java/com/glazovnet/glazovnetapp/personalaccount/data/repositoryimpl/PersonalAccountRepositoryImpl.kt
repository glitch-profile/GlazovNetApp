package com.glazovnet.glazovnetapp.personalaccount.data.repositoryimpl

import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.core.data.utils.ApiResponseDto
import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.personalaccount.data.entity.ClientInfoDto
import com.glazovnet.glazovnetapp.personalaccount.data.mapper.toClientInfo
import com.glazovnet.glazovnetapp.personalaccount.domain.model.ClientInfo
import com.glazovnet.glazovnetapp.personalaccount.domain.repository.PersonalAccountRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.put
import javax.inject.Named

private const val PATH = "/api/account"

class PersonalAccountRepositoryImpl(
    @Named("RestClient") private val client: HttpClient
): PersonalAccountRepository {

    override suspend fun getClientData(token: String, clientId: String): Resource<ClientInfo> {
        return try {
            val response: ApiResponseDto<ClientInfoDto> = client.get("$PATH/info") {
                bearerAuth(token)
                header("client_id", clientId)
            }.body()
            if (response.status) {
                Resource.Success(
                    data = response.data.toClientInfo()
                )
            } else Resource.Error(
                stringResourceId = R.string.personal_account_user_not_found_error
            )
        } catch (e: Exception) {
            Resource.generateFromApiResponseError(e)
        }
    }

    override suspend fun changePassword(
        token: String,
        personId: String,
        oldPassword: String,
        newPassword: String
    ): Resource<Unit> {
        return try {
            val response: ApiResponseDto<Unit> = client.put("$PATH/update-password") {
                bearerAuth(token)
                header("person_id", personId)
                header("old_password", oldPassword)
                header("new_password", newPassword)
            }.body()
            if (response.status) {
                Resource.Success(Unit)
            } else Resource.Error(
                stringResourceId = R.string.personal_account_change_password_failed_message,
            )
        } catch (e: Exception) {
            Resource.generateFromApiResponseError(e)
        }
    }

    override suspend fun changeTariff(
        token: String,
        clientId: String,
        newTariffId: String
    ): Resource<Unit> {
        return try {
            val response: ApiResponseDto<Unit> = client.put("$PATH/update-password") {
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

    override suspend fun blockAccount(token: String, clientId: String): Resource<Unit> {
        return try {
            val response: ApiResponseDto<Unit> = client.put("$PATH/block") {
                bearerAuth(token)
                header("client_id", clientId)
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

    override suspend fun addFunds(token: String, clientId: String, amount: Double, note: String?): Resource<Unit> {
        return try {
            val response: ApiResponseDto<Unit> = client.put("$PATH/add-funds") {
                bearerAuth(token)
                header("client_id", clientId)
                header("amount", amount)
                header("note", note)
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