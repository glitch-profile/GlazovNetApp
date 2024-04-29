package com.glazovnet.glazovnetapp.settings.notifications.domain.repository

import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.settings.notifications.domain.model.NotificationTopicModel

interface NotificationsApiRepository {

    suspend fun getAvailableTopics(
        token: String,
        clientId: String?,
        employeeId: String?
    ): Resource<List<NotificationTopicModel>>

    suspend fun getPersonNotificationStatus(
        token: String,
        personId: String
    ): Resource<Boolean?>

    suspend fun getTopicsForPerson(
        token: String,
        personId: String
    ): Resource<List<String>>

    suspend fun setPersonNotificationStatus(
        token: String,
        personId: String,
        newStatus: Boolean
    ): Resource<Unit>

    suspend fun setTopicsForPerson(
        token: String,
        personId: String,
        clientId: String?,
        employeeId: String?,
        newTopicsList: List<String>
    ): Resource<Unit>

    suspend fun updateFcmToken(
        authToken: String,
        personId: String,
        token: String,
        isExclude: Boolean = false
    ): Resource<Unit>

}