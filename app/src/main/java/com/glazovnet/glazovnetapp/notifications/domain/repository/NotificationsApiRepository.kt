package com.glazovnet.glazovnetapp.notifications.domain.repository

import com.glazovnet.glazovnetapp.core.domain.utils.Resource

interface NotificationsApiRepository {

    suspend fun getClientNotificationsStatus(
        token: String,
        clientId: String
    ): Resource<Boolean?>

    suspend fun getTopicsForClient(
        token: String,
        clientId: String
    ): Resource<List<String>>

    suspend fun setClientNotificationsStatus(
        token: String,
        clientId: String,
        newStatus: Boolean
    ): Resource<Unit>

    suspend fun setTopicsForClient(
        token: String,
        clientId: String,
        newTopicsList: List<String>
    ): Resource<Unit>

    suspend fun updateUserFcmToken(
        token: String,
        clientId: String,
        newToken: String?
    ): Resource<Unit>

}