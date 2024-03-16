package com.glazovnet.glazovnetapp.core.data.repositoryimpl

import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.core.data.utils.ApiResponseDto
import com.glazovnet.glazovnetapp.core.domain.repository.UtilsApiRepository
import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.put
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import java.io.File
import javax.inject.Inject
import javax.inject.Named

private const val PATH = "/api/utils"
private const val UPDATE_FCM_TOKEN_PATH = "/api/clients/update-fcm-token"

class UtilsApiRepositoryImpl @Inject constructor(
    @Named("RestClient") private val client: HttpClient

): UtilsApiRepository {

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

    override suspend fun getIntroImageUrl(): String {
        return try {
            client.get("$PATH/get-intro-image-url").body()
        } catch (e: Exception) {
            ""
        }
    }

    override suspend fun updateUserFcmToken(
        token: String,
        clientId: String,
        newToken: String?
    ): Resource<Unit> {
        return try {
            val response: ApiResponseDto<Unit> = client.put(UPDATE_FCM_TOKEN_PATH) {
                bearerAuth(token)
                header("client_id", clientId)
                header("fcm_token", newToken)
            }.body()
            if (response.status) {
                Resource.Success(data = Unit)
            } else {
                Resource.Error(stringResourceId = R.string.api_response_server_error)
            }
        } catch (e: Exception) {
            Resource.Error(stringResourceId = R.string.api_response_unknown_error)
        }
    }
}