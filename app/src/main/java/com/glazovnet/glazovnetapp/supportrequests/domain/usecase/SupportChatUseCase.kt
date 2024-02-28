package com.glazovnet.glazovnetapp.supportrequests.domain.usecase

import com.glazovnet.glazovnetapp.core.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.supportrequests.domain.model.MessageModel
import com.glazovnet.glazovnetapp.supportrequests.domain.repository.RequestsApiRepository
import javax.inject.Inject

class SupportChatUseCase @Inject constructor(
    private val requestsApiRepository: RequestsApiRepository,
    private val authDataRepository: LocalUserAuthDataRepository
) {

    suspend fun getMessagesForRequest(requestId: String): Resource<List<MessageModel>> {
        val token = authDataRepository.getLoginToken() ?: ""
        return requestsApiRepository.getMessagesForRequest(requestId, token)
    }

    suspend fun initChatSocket(requestId: String): Resource<Unit> {
        val token = authDataRepository.getLoginToken() ?: ""
        return requestsApiRepository.initChatSocket(requestId, token)
    }

    suspend fun sendMessage(messageText: String): Resource<Unit> {
        return requestsApiRepository.sendMessage(messageText)
    }

    fun observeMessages() = requestsApiRepository.observeMessages()

    suspend fun disconnect() = requestsApiRepository.closeChatConnection()


}