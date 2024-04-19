package com.glazovnet.glazovnetapp.supportrequests.presentation.createrequest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.core.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.core.presentation.states.MessageNotificationState
import com.glazovnet.glazovnetapp.core.presentation.states.ScreenState
import com.glazovnet.glazovnetapp.supportrequests.domain.model.SupportRequestModel
import com.glazovnet.glazovnetapp.supportrequests.domain.repository.RequestsApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateRequestViewModel @Inject constructor(
    private val requestsApiRepository: RequestsApiRepository,
    userAuthDataRepository: LocalUserAuthDataRepository
): ViewModel() {

    private val _state = MutableStateFlow(ScreenState<SupportRequestModel>())
    val state = _state.asStateFlow()

    private val _requestTitle = MutableStateFlow("")
    val requestTitle = _requestTitle.asStateFlow()
    private val _requestDescription = MutableStateFlow("")
    val requestDescription = _requestDescription.asStateFlow()
    private val _isNotificationsEnabled = MutableStateFlow(true)
    val isNotificationsEnabled = _isNotificationsEnabled.asStateFlow()

    private val _messageState = MutableStateFlow(MessageNotificationState())
    val messageState = _messageState.asStateFlow()
    private val messageScope = CoroutineScope(Dispatchers.Default + Job())

    private val loginToken = userAuthDataRepository.getLoginToken() ?: ""
    private val personId = userAuthDataRepository.getAssociatedPersonId() ?: ""

    fun addRequest() {
        viewModelScope.launch {
            val title = requestTitle.value
            val description = requestDescription.value
            val isReceiveNotifications = isNotificationsEnabled.value
            if (title.isNotBlank() && description.isNotBlank()) {
                _state.update {
                    it.copy(isUploading = true)
                }
                val request = SupportRequestModel(
                    creatorId = personId,
                    title = title.trim(),
                    description = description.trim(),
                    isNotificationsEnabled = isReceiveNotifications
                )
                val result = requestsApiRepository.addRequest(
                    newRequest = request,
                    token = loginToken
                )
                when (result) {
                    is Resource.Success -> {
                        showMessage(
                            titleRes = R.string.create_request_success_message_title,
                            messageRes = R.string.create_request_response_created
                        )
                    }
                    is Resource.Error -> {
                        showMessage(
                            titleRes = R.string.create_request_error_message_title,
                            messageRes = result.stringResourceId!!
                        )
                    }
                }
                _state.update { it.copy(isUploading = false) }
            } else {
                showMessage(
                    titleRes = R.string.create_request_error_message_title,
                    messageRes = R.string.create_request_message_fields_are_empty
                )
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

    private fun showMessage(titleRes: Int, messageRes: Int) {
        messageScope.coroutineContext.cancelChildren()
        messageScope.launch {
            _messageState.update {
                MessageNotificationState(
                    enabled = true, titleResource = titleRes, additionTextResource = messageRes
                )
            }
            delay(3000L)
            _messageState.update { it.copy(enabled = false) }
        }
    }
}