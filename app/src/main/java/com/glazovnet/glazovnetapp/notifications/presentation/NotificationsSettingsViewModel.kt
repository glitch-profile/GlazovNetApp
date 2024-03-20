package com.glazovnet.glazovnetapp.notifications.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glazovnet.glazovnetapp.core.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.core.presentation.ScreenState
import com.glazovnet.glazovnetapp.notifications.domain.repository.NotificationsApiRepository
import com.glazovnet.glazovnetapp.notifications.domain.repository.NotificationsLocalSettingRepository
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class NotificationsSettingsViewModel @Inject constructor(
    private val localUserAuthDataRepository: LocalUserAuthDataRepository,
    private val notificationsLocalSettingRepository: NotificationsLocalSettingRepository,
    private val notificationsApiRepository: NotificationsApiRepository
): ViewModel() {

    private val _state = MutableStateFlow(ScreenState<Unit>())
    val state = _state.asStateFlow()
    private val _isNotificationsEnabled = MutableStateFlow(ScreenState<Boolean>())
    val isNotificationsEnabled = _isNotificationsEnabled.asStateFlow()
    private val _isNotificationsOnDeviceEnabled = MutableStateFlow(false)
    val isNotificationsOnDeviceEnabled = _isNotificationsOnDeviceEnabled.asStateFlow()
    private val _availableTopics = MutableStateFlow(ScreenState<List<String>>())
    val availableTopics = _availableTopics.asStateFlow()
    private val _selectedTopics = MutableStateFlow(emptyList<String>())
    val selectedTopics = _selectedTopics.asStateFlow()
    private val _isNotificationsPermissionGranted = MutableStateFlow(true)
    val isNotificationsPermissionGranted = _isNotificationsPermissionGranted.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }
            _isNotificationsOnDeviceEnabled.update {
                notificationsLocalSettingRepository.getIsNotificationsEnabledOnDevice()
            }
            loadClientNotificationsStatus()
            if (isNotificationsEnabled.value.stringResourceId !== null) {
                //some error occurred
                _state.update {
                    it.copy(
                        stringResourceId = isNotificationsEnabled.value.stringResourceId,
                        message = isNotificationsEnabled.value.message,
                        isLoading = false
                    )
                }
                this.cancel()
            }
            loadAvailableTopics()
            loadSelectedTopics()
            _state.update {
                it.copy(isLoading = false)
            }
        }
    }

    fun setIsNotificationsPermissionGranted(status: Boolean) {
        _isNotificationsPermissionGranted.update { status }
    }
    fun setIsNotificationsEnabled(newStatus: Boolean) {
        _isNotificationsEnabled.update {
            it.copy(data = newStatus)
        }
    }
    fun setIsNotificationsOnDeviceEnabled(newStatus: Boolean) {
        _isNotificationsOnDeviceEnabled.update { newStatus }
    }
    fun selectTopic(topic: String) {
        val newTopicsList = selectedTopics.value.toMutableList().apply {
            if (!this.contains(topic)) {
                this.add(topic)
            }
        }
        _selectedTopics.update { newTopicsList }
    }

    fun unselectTopic(topic: String) {
        val newTopicsList = selectedTopics.value.toMutableList().apply {
            if (this.contains(topic)) {
                this.remove(topic)
            }
        }
        _selectedTopics.update { newTopicsList }
    }

    private suspend fun loadClientNotificationsStatus() {
        val result = notificationsApiRepository.getClientNotificationsStatus(
            token = localUserAuthDataRepository.getLoginToken() ?: "",
            clientId = localUserAuthDataRepository.getAssociatedUserId() ?: ""
        )
        if (result is Resource.Success) {
            _isNotificationsEnabled.update {
                it.copy(data = result.data)
            }
        } else {
            _isNotificationsEnabled.update {
                it.copy(
                    stringResourceId = result.stringResourceId,
                    message = result.message
                )
            }
        }
    }
    private suspend fun loadAvailableTopics() {
        val result = notificationsApiRepository.getAvailableTopics(
            token = localUserAuthDataRepository.getLoginToken() ?: ""
        )
        if (result is Resource.Success) {
            _availableTopics.update {
                it.copy(data = result.data)
            }
        } else {
            _availableTopics.update {
                it.copy(
                    stringResourceId = result.stringResourceId,
                    message = result.message
                )
            }
        }
    }
    private suspend fun loadSelectedTopics() {
        val result = notificationsApiRepository.getTopicsForClient(
            token = localUserAuthDataRepository.getLoginToken() ?: "",
            clientId = localUserAuthDataRepository.getAssociatedUserId() ?: ""
        )
        _selectedTopics.update { result.data ?: emptyList() }
    }

    fun saveChanges() {
        viewModelScope.launch {
            _state.update {
                it.copy(isUploading = true)
            }
            notificationsApiRepository.setClientNotificationsStatus(
                token = localUserAuthDataRepository.getLoginToken() ?: "",
                clientId = localUserAuthDataRepository.getAssociatedUserId() ?: "",
                newStatus = isNotificationsEnabled.value.data ?: false
            )
            if (isNotificationsOnDeviceEnabled.value) {
                val newToken = Firebase.messaging.token.await()
                notificationsLocalSettingRepository.setLastKnownFcmToken(newToken)
                notificationsLocalSettingRepository.setIsNotificationsEnabledOnDevice(true)
                notificationsApiRepository.updateFcmToken(
                    authToken = localUserAuthDataRepository.getLoginToken() ?: "",
                    clientId = localUserAuthDataRepository.getAssociatedUserId() ?: "",
                    token = newToken
                )
            } else {
                val token = notificationsLocalSettingRepository.getLastKnownFcmToken()
                val isWasEnabledBefore = notificationsLocalSettingRepository.getIsNotificationsEnabledOnDevice()
                if (token !== null && isWasEnabledBefore) {
                    notificationsApiRepository.updateFcmToken(
                        authToken = localUserAuthDataRepository.getLoginToken() ?: "",
                        clientId = localUserAuthDataRepository.getAssociatedUserId() ?: "",
                        token = token,
                        isExclude = true
                    )
                    notificationsLocalSettingRepository.setIsNotificationsEnabledOnDevice(false)
                }
            }
            notificationsApiRepository.setTopicsForClient(
                token = localUserAuthDataRepository.getLoginToken() ?: "",
                clientId = localUserAuthDataRepository.getAssociatedUserId() ?: "",
                newTopicsList = selectedTopics.value
            )
            notificationsLocalSettingRepository.setIsNotificationsSetupComplete(status = true)
            _state.update {
                it.copy(isUploading = false)
            }
        }
    }
}