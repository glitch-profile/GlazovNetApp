package com.glazovnet.glazovnetapp.supportrequests.domain.repository

import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.supportrequests.data.entity.RequestCreatorInfoDto
import com.glazovnet.glazovnetapp.supportrequests.domain.model.MessageModel
import com.glazovnet.glazovnetapp.supportrequests.domain.model.SupportRequestModel
import kotlinx.coroutines.flow.Flow

interface RequestsApiRepository {

    suspend fun getAllRequests(token: String, employeeId: String): Resource<List<SupportRequestModel>>

    suspend fun getRequestsForClient(token: String, clientId: String): Resource<List<SupportRequestModel>>

    suspend fun getRequestById(token: String, requestId: String, clientId: String?, employeeId: String?): Resource<SupportRequestModel?>

    suspend fun getRequestCreatorInfo(token: String, requestId: String, personId: String): Resource<RequestCreatorInfoDto>

    suspend fun getMessagesForRequest(token: String, requestId: String, clientId: String?, employeeId: String?): Resource<List<MessageModel>>

    suspend fun addRequest(
        token: String,
        clientId: String,
        title: String,
        description: String,
        isNotificationsEnabled: Boolean
    ): Resource<SupportRequestModel>

    suspend fun closeRequest(token: String, requestId: String, employeeId: String): Resource<Unit>

    suspend fun reopenRequest(token: String, requestId: String, clientId: String): Resource<Unit>

    suspend fun changeRequestSupporter(token: String, requestId: String, employeeId: String): Resource<Unit>

    suspend fun initRequestsSocket(token: String, personId: String): Resource<Unit>

    fun observeRequests(): Flow<SupportRequestModel>

    suspend fun initChatSocket(token: String, requestId: String, personId: String): Resource<Unit>

    suspend fun sendMessage(message: String): Resource<Unit>

    fun observeMessages(): Flow<MessageModel>

    suspend fun closeRequestsConnection()

    suspend fun closeChatConnection()

}