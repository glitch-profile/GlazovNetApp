package com.glazovnet.glazovnetapp.presentation.supportscreen.requests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glazovnet.glazovnetapp.domain.models.supportrequest.SupportRequestModel
import com.glazovnet.glazovnetapp.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.domain.usecase.SupportRequestsUseCase
import com.glazovnet.glazovnetapp.domain.utils.Resource
import com.glazovnet.glazovnetapp.presentation.ScreenState
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
    private val supportRequestsUseCase: SupportRequestsUseCase,
    private val localUserAuthDataRepository: LocalUserAuthDataRepository
): ViewModel() {

    val isAdmin = localUserAuthDataRepository.getIsUserAsAdmin() ?: false

    private val _state = MutableStateFlow(ScreenState<List<SupportRequestModel>>())
    val state = _state.asStateFlow()

    fun loadRequests() {
        //user don't need to connect to socket. He just need to see his requests
        if (isAdmin) connectToSocket()
        else getAllRequests()
    }

    private fun getRequestsForUser() {

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
            val result = if (isAdmin) supportRequestsUseCase.getAllRequests()
            else supportRequestsUseCase.getRequestsForClient()
            when (result) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            data = result.data
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            stringResourceId = result.stringResourceId,
                            message = result.message
                        )
                    }
                }
            }
        }
    }

    private fun connectToSocket() {
        getAllRequests()
        viewModelScope.launch {
            when (val result = supportRequestsUseCase.initRequestsSocket()) {
                is Resource.Success -> {
                    supportRequestsUseCase.observeRequests()
                        .onEach {request ->
                            val newRequestsList = state.value.data!!.toMutableList().apply {
                                add(0, request)
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
        if (isAdmin) {
            viewModelScope.launch {
                supportRequestsUseCase.disconnect()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }

}