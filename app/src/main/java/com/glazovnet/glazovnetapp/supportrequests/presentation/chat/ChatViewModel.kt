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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val requestsApiRepository: RequestsApiRepository,
    localUserAuthDataRepository: LocalUserAuthDataRepository
): ViewModel() {

    private val _state = MutableStateFlow(ScreenState<List<MessageModel>>())
    val state = _state.asStateFlow()
    private val _request = MutableStateFlow<SupportRequestModel?>(null)
    val request = _request.asStateFlow()
    private val _requestCreatorName = MutableStateFlow<String?>(null)

    private val _messageState = MutableStateFlow(MessageNotificationState())
    val messageState = _messageState.asStateFlow()
    private val messageScope = CoroutineScope(Dispatchers.Default + Job())

    private val loginToken = localUserAuthDataRepository.getLoginToken() ?: ""
    private val personId = localUserAuthDataRepository.getAssociatedPersonId() ?: ""
    private val clientId = localUserAuthDataRepository.getAssociatedClientId() ?: ""
    private val employeeId = localUserAuthDataRepository.getAssociatedEmployeeId() ?: ""

    fun initChat(requestId: String) {
        viewModelScope.launch {
            listOf(
                launch { loadRequest(requestId) },
                launch { loadCreatorName(requestId) }
            ).joinAll() //waiting all tasks for complete
            if (request.value != null) {
                getMessages(requestId)
                if (request.value!!.status != RequestStatus.Solved) {
                    initChatSocket(requestId)
                }
            }
        }
    }

    private suspend fun initChatSocket(requestId: String) {
        val connectionResult = requestsApiRepository.initChatSocket(
            token = loginToken,
            requestId = requestId,
            personId = personId
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

    private suspend fun getMessages(requestId: String) {
        _state.update{
            it.copy(
                isLoading = true,
                stringResourceId = null,
                message = null
            )
        }
        val result = requestsApiRepository.getMessagesForRequest(
            token = loginToken,
            requestId = requestId,
            clientId = clientId.ifEmpty { null },
            employeeId = employeeId.ifEmpty { null }
        )
        when (result) {
            is Resource.Success -> {
                val messagesList = result.data!!.toMutableList().apply {
                    this.add(
                        index = result.data.size,
                        MessageModel(
                            senderId = request.value!!.creatorPersonId,
                            senderName = _requestCreatorName.value!!,
                            text = request.value!!.description,
                            timestamp = request.value!!.creationDate,
                            isOwnMessage = personId == request.value!!.creatorPersonId
                        )
                    )
                }
                _state.update { it.copy(data = messagesList) }
            }
            is Resource.Error -> {
                _state.update { it.copy(message = result.message, stringResourceId = result.stringResourceId) }
            }
        }
        _state.update { it.copy(isLoading = false) }
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

    private suspend fun loadRequest(requestId: String) {
        val result = requestsApiRepository.getRequestById(
            token = loginToken,
            requestId = requestId,
            clientId = clientId.ifEmpty { null },
            employeeId = employeeId.ifEmpty { null }
        )
        if (result is Resource.Success) {
            _request.update { result.data!! }
        } else {
            showMessage(
                titleRes = R.string.request_chat_cant_connect_to_chat_error_title,
                messageRes = result.stringResourceId!!
            )
        }
    }

    private suspend fun loadCreatorName(requestId: String) {
        val result = requestsApiRepository.getRequestCreatorInfo(
            token = loginToken,
            requestId = requestId,
            personId = personId
        )
        if (result is Resource.Success) {
            _requestCreatorName.update { result.data!!.fullName }
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