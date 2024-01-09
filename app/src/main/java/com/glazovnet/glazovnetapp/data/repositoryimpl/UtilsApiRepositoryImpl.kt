package com.glazovnet.glazovnetapp.data.repositoryimpl

import com.example.glazovnetadminapp.entity.authDto.AuthDataDto
import com.example.glazovnetadminapp.entity.authDto.AuthResponse
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.data.entity.ApiResponseDto
import com.glazovnet.glazovnetapp.domain.repository.UtilsApiRepository
import com.glazovnet.glazovnetapp.domain.utils.Resource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import java.io.File
import javax.inject.Inject
import javax.inject.Named

private const val PATH = "/api/utils"
private const val LOGIN_PATH = "/api/login"

class UtilsApiRepositoryImpl @Inject constructor(
    @Named("RestClient") private val client: HttpClient

): UtilsApiRepository {

    override suspend fun login(authData: AuthDataDto): Resource<AuthResponse> {
        return try {
            val response: ApiResponseDto<AuthResponse> = client.post(LOGIN_PATH) {
                contentType(ContentType.Application.Json)
                setBody(authData)
            }.body()
            if (response.status) Resource.Success(data = response.data)
            else Resource.Error(R.string.api_response_server_error, response.message)
        } catch (e: ResponseException) {
            Resource.Error(R.string.api_response_error, e.response.status.toString())
        } catch (e: ConnectTimeoutException) {
            Resource.Error(R.string.api_response_server_not_available)
        } catch (e: Exception) {
            Resource.Error(R.string.api_response_unknown_error)
        }
    }

    override suspend fun uploadImage(file: File, token: String): Resource<List<String>> {
        return try {
            val response: ApiResponseDto<List<String>> = client.submitFormWithBinaryData(
                url = "$PATH/upload-files",
                formData = formData {
                    append("image", file.readBytes(), Headers.build {
                        append(HttpHeaders.ContentType, "image/jpg")
                        append(HttpHeaders.ContentDisposition, "filename=${file.name}")
                    })
                }
            ) {
                bearerAuth(token)
            }.body()
            if (response.status) Resource.Success(data = response.data)
            else Resource.Error(R.string.api_response_server_error, response.message)
        } catch (e: ResponseException) {
            Resource.Error(R.string.api_response_error, e.response.status.toString())
        } catch (e: ConnectTimeoutException) {
            Resource.Error(R.string.api_response_server_not_available)
        } catch (e: Exception) {
            Resource.Error(R.string.api_response_unknown_error)
        }
    }

}