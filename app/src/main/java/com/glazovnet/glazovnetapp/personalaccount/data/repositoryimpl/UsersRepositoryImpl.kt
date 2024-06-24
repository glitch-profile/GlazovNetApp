package com.glazovnet.glazovnetapp.personalaccount.data.repositoryimpl

import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.core.data.utils.ApiResponseDto
import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.personalaccount.data.entity.ClientInfoDto
import com.glazovnet.glazovnetapp.personalaccount.data.entity.EmployeeInfoDto
import com.glazovnet.glazovnetapp.personalaccount.data.entity.PersonInfoDto
import com.glazovnet.glazovnetapp.personalaccount.data.entity.TransactionModelDto
import com.glazovnet.glazovnetapp.personalaccount.data.mapper.toClientModel
import com.glazovnet.glazovnetapp.personalaccount.data.mapper.toEmployeeModel
import com.glazovnet.glazovnetapp.personalaccount.data.mapper.toPersonModel
import com.glazovnet.glazovnetapp.personalaccount.data.mapper.toTransactionModel
import com.glazovnet.glazovnetapp.personalaccount.domain.model.ClientModel
import com.glazovnet.glazovnetapp.personalaccount.domain.model.EmployeeModel
import com.glazovnet.glazovnetapp.personalaccount.domain.model.PersonModel
import com.glazovnet.glazovnetapp.personalaccount.domain.model.TransactionModel
import com.glazovnet.glazovnetapp.personalaccount.domain.repository.UsersRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.put
import javax.inject.Inject
import javax.inject.Named

private const val PERSONS_PATH = "/api/persons"
private const val CLIENTS_PATH = "/api/clients"
private const val EMPLOYEES_PATH = "/api/employees"

class UsersRepositoryImpl @Inject constructor(
    @Named("RestClient") private val client: HttpClient
): UsersRepository {

    override suspend fun getClientData(token: String, clientId: String): Resource<ClientModel> {
        return try {
            val response: ApiResponseDto<ClientInfoDto> = client.get("$CLIENTS_PATH/info") {
                bearerAuth(token)
                header("client_id", clientId)
            }.body()
            if (response.status) {
                Resource.Success(
                    data = response.data.toClientModel()
                )
            } else Resource.Error(
                stringResourceId = R.string.personal_account_user_not_found_error
            )
        } catch (e: Exception) {
            Resource.generateFromApiResponseError(e)
        }
    }

    override suspend fun getPersonData(token: String, personId: String): Resource<PersonModel> {
        return try {
            val response: ApiResponseDto<PersonInfoDto> = client.get("$PERSONS_PATH/info") {
                bearerAuth(token)
                header("person_id", personId)
            }.body()
            if (response.status) {
                Resource.Success(
                    data = response.data.toPersonModel()
                )
            } else Resource.Error(
                stringResourceId = R.string.personal_account_user_not_found_error
            )
        } catch (e: Exception) {
            Resource.generateFromApiResponseError(e)
        }
    }

    override suspend fun getEmployeeData(
        token: String,
        employeeId: String
    ): Resource<EmployeeModel> {
        return try {
            val response: ApiResponseDto<EmployeeInfoDto> = client.get("$EMPLOYEES_PATH/info") {
                bearerAuth(token)
                header("employee_id", employeeId)
            }.body()
            if (response.status) {
                Resource.Success(
                    data = response.data.toEmployeeModel()
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
            val response: ApiResponseDto<Unit> = client.put("$PERSONS_PATH/update-password") {
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

    override suspend fun blockAccount(token: String, clientId: String): Resource<Unit> {
        return try {
            val response: ApiResponseDto<Unit> = client.put("$CLIENTS_PATH/block") {
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
            val response: ApiResponseDto<Unit> = client.put("$CLIENTS_PATH/add-funds") {
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

    override suspend fun loadBalanceHistory(token: String, clientId: String): Resource<List<TransactionModel>> {
        return try {
            val response: ApiResponseDto<List<TransactionModelDto>> = client.get("$CLIENTS_PATH/balance-history") {
                bearerAuth(token)
                header("client_id", clientId)
            }.body()
            if (response.status) {
                Resource.Success(
                    data = response.data.map { it.toTransactionModel() }
                )
            } else Resource.Error(
                stringResourceId = R.string.api_response_server_error
            )
        } catch (e: Exception) {
            Resource.generateFromApiResponseError(e)
        }
    }
}