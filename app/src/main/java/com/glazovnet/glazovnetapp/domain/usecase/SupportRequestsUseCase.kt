package com.glazovnet.glazovnetapp.domain.usecase

import com.glazovnet.glazovnetapp.domain.models.supportrequest.SupportRequestModel
import com.glazovnet.glazovnetapp.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.domain.repository.RequestsApiRepository
import com.glazovnet.glazovnetapp.domain.utils.Resource
import javax.inject.Inject

class SupportRequestsUseCase @Inject constructor(
    private val requestsApiRepository: RequestsApiRepository,
    private val authDataRepository: LocalUserAuthDataRepository
) {

    suspend fun getRequestsForClient(): Resource<List<SupportRequestModel>> {
        val token = authDataRepository.getLoginToken() ?: ""
        return requestsApiRepository.getRequestsForClient(token)
    }

    suspend fun getAllRequests(): Resource<List<SupportRequestModel>> {
        val token = authDataRepository.getLoginToken() ?: ""
        return requestsApiRepository.getAllRequests(token)
    }

    suspend fun getRequestById(requestId: String): Resource<SupportRequestModel?> {
        val token = authDataRepository.getLoginToken() ?: ""
        return requestsApiRepository.getRequestById(requestId, token)
    }

    suspend fun initRequestsSocket(): Resource<Unit> {
        val token = authDataRepository.getLoginToken() ?: ""
        return requestsApiRepository.initRequestsSocket(token)
    }

    fun observeRequests() = requestsApiRepository.observeRequests()

    suspend fun addRequest(newRequestModel: SupportRequestModel): Resource<SupportRequestModel?> {
        val token = authDataRepository.getLoginToken() ?: ""
        return requestsApiRepository.addRequest(newRequestModel, token)
    }

    suspend fun disconnect() {
        requestsApiRepository.closeRequestsConnection()
    }

}