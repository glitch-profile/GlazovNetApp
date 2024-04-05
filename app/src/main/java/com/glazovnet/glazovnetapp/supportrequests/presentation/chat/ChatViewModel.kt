package com.glazovnet.glazovnetapp.supportrequests.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.core.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.core.presentation.states.MessageNotificationState
import com.glazovnet.glazovnetapp.core.presentation.states.ScreenState
import com.glazovnet.glazovnetapp.supportrequests.domain.model.MessageModel
import com.glazovnet.glazovnetapp.supportrequests.domain.model.RequestStatus
import com.glazovnet.glazovnetapp.supportrequests.domain.repository.RequestsApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val requestsApiRepository: RequestsApiRepository,
    private val localUserAuthDataRepository: LocalUserAuthDataRepository
): ViewModel() {

    private val _state = MutableStateFlow(ScreenState<List<MessageModel>>())
    val state = _state.asStateFlow()
    private val _requestStatus = MutableStateFlow<RequestStatus>(RequestStatus.Active)
    val requestStatus = _requestStatus.asStateFlow()

    private val _messageState = MutableStateFlow(MessageNotificationState())
    val messageState = _messageState.asStateFlow()
    private val messageScope = CoroutineScope(Dispatchers.Default + Job())

    fun initChatSocket(requestId: String) {
        getMessages(requestId)
        getRequestStatus(requestId)
        viewModelScope.launch {
            val connectionResult = requestsApiRepository.initChatSocket(
                requestId = requestId,
                token = localUserAuthDataRepository.getLoginToken() ?: ""
            )
            when (connectionResult) {
                is Resource.Success -> {
                    requestsApiRepository.observeMessages()
                        .onEach {message ->
                            if (state.value.data != null) {
                                val newMessageList = state.value.data!!.toMutableList().apply {
                                    add(0, message)
                                }
                                _state.update { it.copy(data = newMessageList) }
                            }
                        }.launchIn(viewModelScope)
                }
                is Resource.Error -> {
                    showMessage(
                        titleRes = R.string.request_chat_cant_connect_to_chat_error_title,
                        messageRes = connectionResult.stringResourceId!!
                    )
                }
            }
        }
    }

    private fun getMessages(requestId: String) {
        viewModelScope.launch {
            _state.update{
                it.copy(
                    isLoading = true,
                    stringResourceId = null,
                    message = null
                )
            }
            val result = requestsApiRepository.getMessagesForRequest(
                requestId = requestId,
                token = localUserAuthDataRepository.getLoginToken() ?: ""
            )
            when (result) {
                is Resource.Success -> {
                    _state.update { it.copy(data = result.data) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(message = result.message, stringResourceId = result.stringResourceId) }
                }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun sendMessage(message: String) {
        viewModelScope.launch {
            if (message.isNotBlank()) {
                val formattedMessage = message.trim()
                val result = requestsApiRepository.sendMessage(formattedMessage)
                if (result is Resource.Error) {
                    showMessage(
                        titleRes = R.string.request_chat_cant_send_message_error_title,
                        messageRes = result.stringResourceId!!
                    )
                }
            }
        }
    }

    private fun getRequestStatus(requestId: String) {
        viewModelScope.launch {
            val result = requestsApiRepository.getRequestById(
                requestId = requestId,
                token = localUserAuthDataRepository.getLoginToken() ?: ""
            )
            if (result is Resource.Success) {
                _requestStatus.update { result.data!!.status }
            } else _requestStatus.update { RequestStatus.Solved }
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            requestsApiRepository.closeChatConnection()
        }
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
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