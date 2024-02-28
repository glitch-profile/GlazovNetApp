package com.glazovnet.glazovnetapp.login.data.repositoryimpl

import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.core.data.utils.ApiResponseDto
import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.login.data.entity.AuthDataDto
import com.glazovnet.glazovnetapp.login.data.entity.AuthResponse
import com.glazovnet.glazovnetapp.login.domain.repository.LoginApiRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject
import javax.inject.Named

private const val LOGIN_PATH = "/api/login"

class LoginApiRepositoryImpl @Inject constructor(
    @Named("RestClient") private val client: HttpClient

): LoginApiRepository {

    override suspend fun login(authData: AuthDataDto): Resource<AuthResponse> {
        return try {
            val response: ApiResponseDto<AuthResponse?> = client.post(LOGIN_PATH) {
                contentType(ContentType.Application.Json)
                setBody(authData)
            }.body()
            if (response.status) Resource.Success(data = response.data!!)
            else if (response.message == "user not found") {
                Resource.Error(R.string.login_response_user_not_found)
            } else Resource.Error(R.string.api_response_server_error, response.message)
        } catch (e: ResponseException) {
            Resource.Error(R.string.api_response_error, e.response.status.toString())
        } catch (e: ConnectTimeoutException) {
            Resource.Error(R.string.api_response_server_not_available)
        } catch (e: Exception) {
            Resource.Error(R.string.api_response_unknown_error)
        }
    }

}