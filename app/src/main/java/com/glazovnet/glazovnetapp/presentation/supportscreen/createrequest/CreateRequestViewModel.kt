package com.glazovnet.glazovnetapp.presentation.supportscreen.createrequest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.domain.models.supportrequest.SupportRequestModel
import com.glazovnet.glazovnetapp.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.domain.repository.RequestsApiRepository
import com.glazovnet.glazovnetapp.domain.utils.Resource
import com.glazovnet.glazovnetapp.presentation.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateRequestViewModel @Inject constructor(
    private val requestsApiRepository: RequestsApiRepository,
    private val userAuthDataRepository: LocalUserAuthDataRepository
): ViewModel() {

    private val _state = MutableStateFlow(ScreenState<SupportRequestModel>())
    val state = _state.asStateFlow()

    private val _requestTitle = MutableStateFlow("")
    val requestTitle = _requestTitle.asStateFlow()
    private val _requestDescription = MutableStateFlow("")
    val requestDescription = _requestDescription.asStateFlow()
    private val _isNotificationsEnabled = MutableStateFlow(true)
    val isNotificationsEnabled = _isNotificationsEnabled.asStateFlow()

    private val _messageChannel = Channel<Int>()
    val messageChannel = _messageChannel.receiveAsFlow()

    fun addRequest() {
        viewModelScope.launch {
            val title = requestTitle.value
            val description = requestDescription.value
            val isReceiveNotifications = isNotificationsEnabled.value
            if (title.isNotBlank() && description.isNotBlank()) {
                _state.update {
                    it.copy(isUploading = true)
                }
                val userId = userAuthDataRepository.getAssociatedUserId() ?: ""
                val request = SupportRequestModel(
                    creatorId = userId,
                    title = title.trim(),
                    description = description.trim(),
                    isNotificationsEnabled = isReceiveNotifications
                )
                val result = requestsApiRepository.addRequest(
                    newRequest = request,
                    token = userAuthDataRepository.getLoginToken() ?: ""
                )
                when (result) {
                    is Resource.Success -> {
                        _messageChannel.send(R.string.create_request_response_created)
                    }
                    is Resource.Error -> {
                        _messageChannel.send(result.stringResourceId!!)
                    }
                }
                _state.update { it.copy(isUploading = false) }
            } else {
                _messageChannel.send(R.string.create_request_message_fields_are_empty)
            }
        }
    }

    fun updateRequestTitle(text: String) {
        _requestTitle.update { text }
    }
    fun updateRequestDescription(text: String) {
        _requestDescription.update { text }
    }
    fun updateRequestNotificationSettings(enabled: Boolean) {
        _isNotificationsEnabled.update { enabled }
    }

}