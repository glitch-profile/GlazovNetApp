package com.glazovnet.glazovnetapp.supportrequests.presentation.requests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glazovnet.glazovnetapp.core.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.core.domain.utils.EmployeeRoles
import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.core.presentation.states.ScreenState
import com.glazovnet.glazovnetapp.supportrequests.domain.model.SupportRequestModel
import com.glazovnet.glazovnetapp.supportrequests.domain.repository.RequestsApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RequestsViewModel @Inject constructor(
    private val requestsApiRepository: RequestsApiRepository,
    localUserAuthDataRepository: LocalUserAuthDataRepository
): ViewModel() {

    val isEmployeeWithRole = localUserAuthDataRepository.getEmployeeHasRole(EmployeeRoles.SUPPORT_CHAT)
    private val loginToken = localUserAuthDataRepository.getLoginToken() ?: ""
    private val personId = localUserAuthDataRepository.getAssociatedPersonId() ?: ""
    private val clientId = localUserAuthDataRepository.getAssociatedClientId()
    private val employeeId = localUserAuthDataRepository.getAssociatedEmployeeId()


    private val _state = MutableStateFlow(ScreenState<List<SupportRequestModel>>())
    val state = _state.asStateFlow()

    fun loadRequests() {
        //user don't need to connect to socket. He just need to see his requests
        if (isEmployeeWithRole) connectToSocket()
        else getAllRequests()
    }

    private fun getAllRequests() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    stringResourceId = null,
                    message = null
                )
            }
            val result = if (isEmployeeWithRole) {
                requestsApiRepository.getAllRequests(loginToken, employeeId ?: "")
            }
            else {
                requestsApiRepository.getRequestsForClient(loginToken, clientId ?: "")
            }
            when (result) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            data = result.data
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            data = null,
                            stringResourceId = result.stringResourceId,
                            message = result.message
                        )
                    }
                }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun connectToSocket() {
        getAllRequests()
        viewModelScope.launch {
            when (val result = requestsApiRepository.initRequestsSocket(loginToken, personId)) {
                is Resource.Success -> {
                    requestsApiRepository.observeRequests()
                        .onEach {request ->
                            val newRequestsList = state.value.data!!.toMutableList().apply {
                                if (this.any { it.id == request.id }) {
                                    val index = this.indexOfFirst { it.id == request.id }
                                    this[index] = request
                                } else {
                                    add(0, request)
                                }
                            }
                            _state.update {
                                it.copy(data = newRequestsList)
                            }
                        }.launchIn(viewModelScope)
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            stringResourceId = result.stringResourceId,
                            message = result.message
                        )
                    }
                }
            }
        }
    }

    fun disconnect() {
        if (isEmployeeWithRole) {
            viewModelScope.launch {
                requestsApiRepository.closeRequestsConnection()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }

}