package com.glazovnet.glazovnetapp.presentation.supportscreen.requestdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
                        it.copy(message = result.message, stringResourceId = result.stringResourceId)
                    }
                }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun markRequestAsSolved() {
//        TODO
    }

    fun assignSupporter() {
//        TODO
    }
}