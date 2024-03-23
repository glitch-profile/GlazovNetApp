package com.glazovnet.glazovnetapp.notifications.data.repository

import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.core.data.utils.ApiResponseDto
import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.notifications.data.dto.NotificationTopicDto
import com.glazovnet.glazovnetapp.notifications.data.mapper.toNotificationTopicModel
import com.glazovnet.glazovnetapp.notifications.domain.model.NotificationTopicModel
import com.glazovnet.glazovnetapp.notifications.domain.repository.NotificationsApiRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.put
import javax.inject.Inject
import javax.inject.Named

private const val PATH = "/api/notifications"

class NotificationsApiRepositoryImpl @Inject constructor(
    @Named("RestClient") private val client: HttpClient
): NotificationsApiRepository {

    override suspend fun getAvailableTopics(token: String): Resource<List<NotificationTopicModel>> {
        return try {
            val result: ApiResponseDto<List<NotificationTopicDto>> = client.get("$PATH/get-topics") {
                bearerAuth(token)
            }.body()
            if (result.status) {
                Resource.Success(
                    data = result.data.map { it.toNotificationTopicModel() }
                )
            } else Resource.Error(
                stringResourceId = R.string.api_response_error
            )
        } catch (e: Exception) {
            println(e)
            Resource.Error(
                stringResourceId = R.string.api_response_unknown_error
            )
        }
    }

    override suspend fun getClientNotificationsStatus(
        token: String,
        clientId: String
    ): Resource<Boolean?> {
        return try {
            val result: ApiResponseDto<Boolean?> = client.get("$PATH/get-client-notifications-status") {
                bearerAuth(token)
                header("client_id", clientId)
            }.body()
            if (result.status) {
                Resource.Success(
                    data = result.data
                )
            } else Resource.Error(
                stringResourceId = R.string.api_response_error
            )
        } catch (e: Exception) {
            Resource.generateFromApiResponseError(e)
        }
    }

    override suspend fun getTopicsForClient(
        token: String,
        clientId: String
    ): Resource<List<String>> {
        return try {
            val result: ApiResponseDto<List<String>> = client.get("$PATH/get-topics-for-client") {
                bearerAuth(token)
                header("client_id", clientId)
            }.body()
            if (result.status) {
                Resource.Success(
                    data = result.data
                )
            } else Resource.Error(
                stringResourceId = R.string.api_response_error
            )
        } catch (e: Exception) {
            Resource.Error(
                stringResourceId = R.string.api_response_unknown_error
            )
        }
    }

    override suspend fun setClientNotificationsStatus(
        token: String,
        clientId: String,
        newStatus: Boolean
    ): Resource<Unit> {
        return try {
            val result: ApiResponseDto<Unit> = client.put("$PATH/set-client-notification-status") {
                bearerAuth(token)
                header("client_id", clientId)
                parameter("status", newStatus)
            }.body()
            if (result.status) {
                Resource.Success(
                    data = result.data
                )
            } else Resource.Error(
                stringResourceId = R.string.api_response_error
            )
        } catch (e: Exception) {
            Resource.Error(
                stringResourceId = R.string.api_response_unknown_error
            )
        }
    }

    override suspend fun setTopicsForClient(
        token: String,
        clientId: String,
        newTopicsList: List<String>
    ): Resource<Unit> {
        return try {
            val result: ApiResponseDto<Unit> = client.put("$PATH/update-client-subscribed-topics") {
                bearerAuth(token)
                header("client_id", clientId)
                parameter("topics", newTopicsList.joinToString(","))
            }.body()
            if (result.status) {
                Resource.Success(
                    data = result.data
                )
            } else Resource.Error(
                stringResourceId = R.string.api_response_error
            )
        } catch (e: Exception) {
            Resource.Error(
                stringResourceId = R.string.api_response_unknown_error
            )
        }
    }

    override suspend fun updateFcmToken(
        authToken: String,
        clientId: String,
        token: String,
        isExclude: Boolean
    ): Resource<Unit> {
        return try {
            val result: ApiResponseDto<Unit> = client.put("$PATH/update-client-fcm-token") {
                bearerAuth(authToken)
                header("client_id", clientId)
                header("fcm_token", token)
                    .apply {
                        if (isExclude) parameter("exclude", true)
                    }
            }.body()
            if (result.status) {
                Resource.Success(
                    data = result.data
                )
            } else Resource.Error(
                stringResourceId = R.string.api_response_error
            )
        } catch (e: Exception) {
            Resource.Error(
                stringResourceId = R.string.api_response_unknown_error
            )
        }
    }
}