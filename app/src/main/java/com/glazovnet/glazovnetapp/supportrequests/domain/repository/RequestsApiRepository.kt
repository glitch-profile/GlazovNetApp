package com.glazovnet.glazovnetapp.supportrequests.domain.repository

import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.supportrequests.domain.model.MessageModel
import com.glazovnet.glazovnetapp.supportrequests.domain.model.RequestStatus
import com.glazovnet.glazovnetapp.supportrequests.domain.model.SupportRequestModel
import kotlinx.coroutines.flow.Flow

interface RequestsApiRepository {

    suspend fun getAllRequests(token: String): Resource<List<SupportRequestModel>>

    suspend fun getRequestsForClient(token: String): Resource<List<SupportRequestModel>>

    suspend fun getRequestById(requestId: String, token: String): Resource<SupportRequestModel?>

    suspend fun getMessagesForRequest(requestId: String, token: String): Resource<List<MessageModel>>

    suspend fun addRequest(newRequest: SupportRequestModel, token: String): Resource<SupportRequestModel?>

    suspend fun changeRequestStatus(requestId: String, newStatus: RequestStatus, token: String): Resource<Unit>

    suspend fun changeRequestSupporter(requestId: String, newSupporterId: String, token: String): Resource<Unit>

    suspend fun initRequestsSocket(token: String): Resource<Unit>

    fun observeRequests(): Flow<SupportRequestModel>

    suspend fun initChatSocket(requestId: String, token: String): Resource<Unit>

    suspend fun sendMessage(message: String): Resource<Unit>

    fun observeMessages(): Flow<MessageModel>

    suspend fun closeRequestsConnection()

    suspend fun closeChatConnection()

}