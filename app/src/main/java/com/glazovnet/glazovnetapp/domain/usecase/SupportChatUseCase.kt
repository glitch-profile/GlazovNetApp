package com.glazovnet.glazovnetapp.domain.usecase

import com.glazovnet.glazovnetapp.domain.models.supportrequest.MessageModel
import com.glazovnet.glazovnetapp.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.domain.repository.RequestsApiRepository
import com.glazovnet.glazovnetapp.domain.utils.Resource
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