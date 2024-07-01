package com.glazovnet.glazovnetapp.supportrequests.presentation.requestdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glazovnet.glazovnetapp.core.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.core.domain.utils.EmployeeRoles
import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.core.presentation.states.ScreenState
import com.glazovnet.glazovnetapp.supportrequests.data.entity.RequestCreatorInfoDto
import com.glazovnet.glazovnetapp.supportrequests.domain.model.RequestStatus
import com.glazovnet.glazovnetapp.supportrequests.domain.model.SupportRequestModel
import com.glazovnet.glazovnetapp.supportrequests.domain.repository.RequestsApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class RequestDetailsViewModel @Inject constructor(
    private val requestsApiRepository: RequestsApiRepository,
    userAuthDataRepository: LocalUserAuthDataRepository
): ViewModel() {

    private val _state = MutableStateFlow(ScreenState<SupportRequestModel>())
    val state = _state.asStateFlow()
    private val _creatorInfo = MutableStateFlow<RequestCreatorInfoDto?>(null)
    val creatorInfo = _creatorInfo.asStateFlow()

    val employeeId = userAuthDataRepository.getAssociatedEmployeeId() ?: ""
    private val loginToken = userAuthDataRepository.getLoginToken() ?: ""
    val clientId = userAuthDataRepository.getAssociatedClientId() ?: ""
    val isEmployeeWithRole = userAuthDataRepository.getEmployeeHasRole(EmployeeRoles.SUPPORT_CHAT)

    fun loadRequestInfo(requestId: String) {
        loadRequestDetails(requestId)
        loadCreatorInfo(requestId)
    }

    private fun loadRequestDetails(requestId: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true, message = null, stringResourceId = null)
            }
            val result = requestsApiRepository.getRequestById(
                requestId = requestId,
                token = loginToken,
                clientId = clientId.ifEmpty { null },
                employeeId = employeeId.ifEmpty { null }
            )
            when (result) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(data = result.data)
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            data = null,
                            message = result.message,
                            stringResourceId = result.stringResourceId
                        )
                    }
                }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun loadCreatorInfo(requestId: String) {
        if (isEmployeeWithRole) {
            viewModelScope.launch {
                val result = requestsApiRepository.getRequestCreatorInfo(
                    token = loginToken,
                    requestId = requestId,
                    employeeId = employeeId
                )
                if (result is Resource.Success) {
                    _creatorInfo.update { result.data }
                }
            }
        }
    }

    fun closeRequest() {
        viewModelScope.launch {
            val currentRequest = state.value.data
            val newStatus = RequestStatus.Solved
            if (currentRequest !== null && currentRequest.status !== newStatus) {
                _state.update { it.copy(isUploading = true) }
                val updatedRequest = currentRequest.copy(
                    status = newStatus
                )
                val result = requestsApiRepository.closeRequest(
                    token = loginToken,
                    requestId = currentRequest.id,
                    employeeId = employeeId
                )
                if (result is Resource.Success) _state.update { it.copy(updatedRequest) }
                else _state.update { it.copy(message = result.message, stringResourceId = result.stringResourceId) }
                _state.update { it.copy(isUploading = false) }
            }
        }
    }

    fun assignSupporter() {
        viewModelScope.launch {
            val currentRequest = state.value.data
            if (currentRequest !== null) {
                if (isEmployeeWithRole) {
                    _state.update { it.copy(isUploading = true) }
                    val updatedRequest = currentRequest.copy(
                        associatedSupportId = employeeId,
                        status = RequestStatus.Active
                    )
                    val result = requestsApiRepository.changeRequestSupporter(
                        token = loginToken,
                        requestId = currentRequest.id,
                        employeeId = employeeId
                    )
                    if (result is Resource.Success) _state.update { it.copy(updatedRequest) }
                    else _state.update { it.copy(message = result.message, stringResourceId = result.stringResourceId) }
                    _state.update { it.copy(isUploading = false) }
                }
            }
        }
    }

    fun reopenRequest() {
        viewModelScope.launch {
            _state.update { it.copy(isUploading = true) }
            val currentRequest = state.value.data
            if (currentRequest != null) {
                val result = requestsApiRepository.reopenRequest(
                    token = loginToken,
                    requestId = currentRequest.id,
                    clientId = clientId
                )
                if (result is Resource.Success) {
                    val updatedRequest = currentRequest.copy(
                        status = RequestStatus.NotReviewed,
                        associatedSupportId = null,
                        reopenDate = OffsetDateTime.now(ZoneId.systemDefault())
                    )
                    _state.update { it.copy(data = updatedRequest) }
                } else _state.update { it.copy(message = result.message, stringResourceId = result.stringResourceId) }
                _state.update { it.copy(isUploading = false) }
            }
        }
    }
}