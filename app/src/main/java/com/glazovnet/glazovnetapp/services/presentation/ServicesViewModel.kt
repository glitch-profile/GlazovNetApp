package com.glazovnet.glazovnetapp.services.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glazovnet.glazovnetapp.core.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.core.presentation.states.ScreenState
import com.glazovnet.glazovnetapp.services.domain.model.ServiceModel
import com.glazovnet.glazovnetapp.services.domain.repository.ServicesApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServicesViewModel @Inject constructor(
    private val servicesApiRepository: ServicesApiRepository,
    localUserAuthDataRepository: LocalUserAuthDataRepository
): ViewModel() {

    private val _state = MutableStateFlow(ScreenState<Unit>())
    val state = _state.asStateFlow()
    private val _availableServices = MutableStateFlow<List<ServiceModel>>(emptyList())
    val availableServices = _availableServices.asStateFlow()
    private val _unavailableServices = MutableStateFlow<List<ServiceModel>>(emptyList())
    val unavailableServices = _unavailableServices.asStateFlow()

    private val _currentOpenService = MutableStateFlow<ServiceDetailsModel?>(null)
    val currentOpenService = _currentOpenService.asStateFlow()

    private val _connectedServices = MutableStateFlow(emptyList<String>())
    val connectedServices = _connectedServices.asStateFlow()

    private val userToken = localUserAuthDataRepository.getLoginToken() ?: ""
    private val clientId = localUserAuthDataRepository.getAssociatedClientId() ?: ""

    init {
        loadServices()
    }

    fun loadServices() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true, message = null, stringResourceId = null)
            }
            val result = servicesApiRepository.getAllServices(userToken)
            if (result is Resource.Success) {
                splitServices(result.data!!)
                getConnectedServices()
                _state.update { it.copy(data = Unit) }
            } else {
                _state.update {
                    it.copy(message = result.message, stringResourceId = result.stringResourceId)
                }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun splitServices(services: List<ServiceModel>) {
        val groupedServices = services.groupBy { it.isActive }
        _availableServices.update { groupedServices[true] ?: emptyList() }
        _unavailableServices.update { groupedServices[false] ?: emptyList() }
    }

    private suspend fun getConnectedServices() {
        val result = servicesApiRepository.getConnectedServicesIds(
            token = userToken,
            clientId = clientId
        )
        if (result is Resource.Success) {
            _connectedServices.update { result.data!! }
        }
    }

    fun openDetailsScreen(serviceId: String) {
        val service = availableServices.value.first { it.id == serviceId }
        val isConnected = connectedServices.value.contains(service.id)
        _currentOpenService.update {
            ServiceDetailsModel(
                service = service,
                isServiceConnected = isConnected
            )
        }
    }
    fun closeDetailsScreen() {
        _currentOpenService.update { null }
    }

    fun connectService(serviceId: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(isUploading = true)
            }
            val result = servicesApiRepository.connectService(
                token = userToken,
                clientId = clientId,
                serviceId = serviceId
            )
            if (result is Resource.Success) {
                addConnectedServiceId(serviceId)
                closeDetailsScreen()
            } else {
                _state.update {
                    it.copy(message = result.message, stringResourceId = result.stringResourceId)
                } //TODO: Replace with message widget
            }
            _state.update {
                it.copy(isUploading = false)
            }
        }
    }

    fun disconnectService(serviceId: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(isUploading = true)
            }
            val result = servicesApiRepository.disconnectService(
                token = userToken,
                clientId = clientId,
                serviceId = serviceId
            )
            if (result is Resource.Success) {
                removeConnectedServiceId(serviceId)
                closeDetailsScreen()
            } else {
                _state.update {
                    it.copy(message = result.message, stringResourceId = result.stringResourceId)
                } //TODO: Replace with message widget
            }
            _state.update {
                it.copy(isUploading = false)
            }
        }
    }

    private fun addConnectedServiceId(serviceId: String) {
        val newConnectedServicesList = connectedServices.value.toMutableList().apply {
            if (!this.contains(serviceId)) this.add(serviceId)
        }
        _connectedServices.update { newConnectedServicesList }
    }
    private fun removeConnectedServiceId(serviceId: String) {
        val newConnectedServicesList = connectedServices.value.toMutableList().apply {
            if (this.contains(serviceId)) this.remove(serviceId)
        }
        _connectedServices.update { newConnectedServicesList }
    }
}