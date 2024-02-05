package com.glazovnet.glazovnetapp.presentation.supportscreen.requestdetails

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.domain.models.supportrequest.RequestStatus
import com.glazovnet.glazovnetapp.domain.models.supportrequest.SupportRequestModel
import com.glazovnet.glazovnetapp.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.domain.repository.RequestsApiRepository
import com.glazovnet.glazovnetapp.domain.utils.Resource
import com.glazovnet.glazovnetapp.presentation.ScreenState
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

    val isAdmin = userAuthDataRepository.getIsUserAsAdmin() ?: false

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
                val result = requestsApiRepository.editRequest(
                    newRequest = updatedRequest,
                    token = userAuthDataRepository.getLoginToken() ?: ""
                )
                if (result is Resource.Success) _state.update { it.copy(updatedRequest) }
                _state.update { it.copy(isUploading = false) }
            }
        }
    }

    fun assignSupporter() { //TODO Rework method to new request. In request on server-side check if supporterId is valid
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
                    val result = requestsApiRepository.editRequest(
                        newRequest = updatedRequest,
                        token = userAuthDataRepository.getLoginToken() ?: ""
                    )
                    if (result is Resource.Success) _state.update { it.copy(updatedRequest) }
                    else _state.update { it.copy(message = result.message, stringResourceId = result.stringResourceId) }
                    _state.update { it.copy(isUploading = false) }
                }
            }
        }
    }

    fun getAssignedSupporterText(): Int {
        Log.i("TAG", "getAssignedSupporterText: getting assigned supporter text")
        val request = state.value.data!!
        val currentUserId = userAuthDataRepository.getAssociatedUserId()
        return when (request.associatedSupportId) {
            null -> R.string.request_details_screen_assigned_supporter_no
            currentUserId -> R.string.request_details_screen_assigned_supporter_you
            else -> R.string.request_details_screen_assigned_supporter_someone
        }
    }
}