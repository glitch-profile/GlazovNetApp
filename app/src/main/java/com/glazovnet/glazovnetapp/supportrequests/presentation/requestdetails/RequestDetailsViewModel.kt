package com.glazovnet.glazovnetapp.supportrequests.presentation.requestdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glazovnet.glazovnetapp.core.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.core.presentation.ScreenState
import com.glazovnet.glazovnetapp.supportrequests.domain.model.RequestStatus
import com.glazovnet.glazovnetapp.supportrequests.domain.model.SupportRequestModel
import com.glazovnet.glazovnetapp.supportrequests.domain.repository.RequestsApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RequestDetailsViewModel @Inject constructor(
    private val requestsApiRepository: RequestsApiRepository,
    private val userAuthDataRepository: LocalUserAuthDataRepository
): ViewModel() {

    private val _state = MutableStateFlow(ScreenState<SupportRequestModel>())
    val state = _state.asStateFlow()

    val isAdmin = userAuthDataRepository.getIsUserAsAdmin()
    val userId = userAuthDataRepository.getAssociatedUserId() ?: ""

    fun loadRequestDetails(requestId: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true, message = null, stringResourceId = null)
            }
            val result = requestsApiRepository.getRequestById(
                requestId = requestId,
                token = userAuthDataRepository.getLoginToken() ?: ""
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

    fun changeRequestStatus(newStatus: RequestStatus) {
        viewModelScope.launch {
            val currentRequest = state.value.data
            if (currentRequest !== null && currentRequest.status !== newStatus) {
                _state.update { it.copy(isUploading = true) }
                val updatedRequest = currentRequest.copy(
                    status = newStatus
                )
                val result = requestsApiRepository.changeRequestStatus(
                    requestId = currentRequest.id,
                    newStatus = newStatus,
                    token = userAuthDataRepository.getLoginToken() ?: ""
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
                if (isAdmin) {
                    _state.update { it.copy(isUploading = true) }
                    val currentAdminId = userAuthDataRepository.getAssociatedUserId() ?: ""
                    val updatedRequest = currentRequest.copy(
                        associatedSupportId = currentAdminId,
                        status = RequestStatus.Active
                    )
                    val result = requestsApiRepository.changeRequestSupporter(
                        requestId = currentRequest.id,
                        newSupporterId = currentAdminId,
                        token = userAuthDataRepository.getLoginToken() ?: ""
                    )
                    if (result is Resource.Success) _state.update { it.copy(updatedRequest) }
                    else _state.update { it.copy(message = result.message, stringResourceId = result.stringResourceId) }
                    _state.update { it.copy(isUploading = false) }
                }
            }
        }
    }
}