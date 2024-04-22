package com.glazovnet.glazovnetapp.settings.notifications.data.repository

import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.core.data.utils.ApiResponseDto
import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.settings.notifications.data.dto.NotificationTopicDto
import com.glazovnet.glazovnetapp.settings.notifications.data.mapper.toNotificationTopicModel
import com.glazovnet.glazovnetapp.settings.notifications.domain.model.NotificationTopicModel
import com.glazovnet.glazovnetapp.settings.notifications.domain.repository.NotificationsApiRepository
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

    override suspend fun getAvailableTopics(
        token: String,
        includeClientsTopics: Boolean,
        includeEmployeeTopics: Boolean
    ): Resource<List<NotificationTopicModel>> {
        return try {
            val result: ApiResponseDto<List<NotificationTopicDto>> = client.get("$PATH/get-topics") {
                bearerAuth(token)
                header("include_client", includeClientsTopics)
                header("include_employee", includeEmployeeTopics)
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

    override suspend fun getPersonNotificationStatus(
        token: String,
        personId: String
    ): Resource<Boolean?> {
        return try {
            val result: ApiResponseDto<Boolean?> = client.get("$PATH/get-person-notifications-status") {
                bearerAuth(token)
                header("person_id", personId)
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

    override suspend fun getTopicsForPerson(
        token: String,
        personId: String
    ): Resource<List<String>> {
        return try {
            val result: ApiResponseDto<List<String>> = client.get("$PATH/get-topics-for-person") {
                bearerAuth(token)
                header("person_id", personId)
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

    override suspend fun setPersonNotificationStatus(
        token: String,
        personId: String,
        newStatus: Boolean
    ): Resource<Unit> {
        return try {
            val result: ApiResponseDto<Unit> = client.put("$PATH/set-person-notification-status") {
                bearerAuth(token)
                header("person_id", personId)
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

    override suspend fun setTopicsForPerson(
        token: String,
        personId: String,
        newTopicsList: List<String>
    ): Resource<Unit> {
        return try {
            val result: ApiResponseDto<Unit> = client.put("$PATH/update-person-subscribed-topics") {
                bearerAuth(token)
                header("person_id", personId)
                if (newTopicsList.isNotEmpty()) parameter("topics", newTopicsList.joinToString(","))
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
        personId: String,
        token: String,
        isExclude: Boolean
    ): Resource<Unit> {
        return try {
            val result: ApiResponseDto<Unit> = client.put("$PATH/update-person-fcm-token") {
                bearerAuth(authToken)
                header("person_id", personId)
                header("fcm_token", token)
                if (isExclude) parameter("exclude", true)
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