package com.glazovnet.glazovnetapp.presentation.supportscreen.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glazovnet.glazovnetapp.domain.models.supportrequest.MessageModel
import com.glazovnet.glazovnetapp.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.domain.repository.RequestsApiRepository
import com.glazovnet.glazovnetapp.domain.utils.Resource
import com.glazovnet.glazovnetapp.presentation.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
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
    private val _messageResourceStringChannel = Channel<Int>()
    val messageResourceStringChannel = _messageResourceStringChannel.receiveAsFlow()

    fun initChatSocket(requestId: String) {
        getMessages(requestId)
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
                    _state.update {
                        it.copy(stringResourceId = connectionResult.stringResourceId, message = connectionResult.message)
                    }
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
                    _messageResourceStringChannel.send(result.stringResourceId!!)
                }
            }
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

}