package com.glazovnet.glazovnetapp.personalaccount.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glazovnet.glazovnetapp.core.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.core.presentation.states.ScreenState
import com.glazovnet.glazovnetapp.personalaccount.domain.model.ClientModel
import com.glazovnet.glazovnetapp.personalaccount.domain.model.EmployeeModel
import com.glazovnet.glazovnetapp.personalaccount.domain.model.PersonModel
import com.glazovnet.glazovnetapp.personalaccount.domain.repository.PersonalAccountRepository
import com.glazovnet.glazovnetapp.services.domain.model.ServiceModel
import com.glazovnet.glazovnetapp.services.domain.repository.ServicesApiRepository
import com.glazovnet.glazovnetapp.tariffs.domain.model.TariffModel
import com.glazovnet.glazovnetapp.tariffs.domain.repository.TariffsApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val personalAccountRepository: PersonalAccountRepository,
    private val tariffsApiRepository: TariffsApiRepository,
    private val servicesApiRepository: ServicesApiRepository,
    userAuthDataRepository: LocalUserAuthDataRepository,
): ViewModel() {

    private val _state = MutableStateFlow(PersonalAccountScreenState())
    val state = _state.asStateFlow()

    private val _tariffData = MutableStateFlow(ScreenState<TariffModel>())
    val tariffData = _tariffData.asStateFlow()

    private val _servicesData = MutableStateFlow(ScreenState<List<ServiceModel>>())
    val servicesData = _servicesData.asStateFlow()

    private val userToken = userAuthDataRepository.getLoginToken() ?: ""
    private val personId = userAuthDataRepository.getAssociatedPersonId()
    private val clientId = userAuthDataRepository.getAssociatedClientId()
    private val employeeId = userAuthDataRepository.getAssociatedEmployeeId()

    init {
        loadUserInfo()
    }

    fun loadUserInfo() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true, stringResourceMessage = null, message = null)
            }
            try {
                val person = loadPersonInfo()
                val client = loadClientInfo()
                val employee = loadEmployeeInfo()
                _state.update {
                    it.copy(
                        isLoading = false,
                        personInfo = person,
                        clientInfo = client,
                        employeeInfo = employee
                    )
                }
                loadTariff()
                loadServices()
            } catch (e: UserRequestError) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        stringResourceMessage = e.stringResource,
                        message = e.message
                    )
                }
            }
        }
    }

    private suspend fun loadTariff() {
        if (state.value.clientInfo != null) {
            _tariffData.update {
                it.copy(isLoading = true)
            }
            val result = tariffsApiRepository.getTariffById(
                token = userToken,
                tariffId = state.value.clientInfo!!.tariffId
            )
            if (result is Resource.Success) {
                _tariffData.update { it.copy(data = result.data!!) }
            }
            _tariffData.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun loadServices() {
        if (state.value.clientInfo != null) {
            _servicesData.update {
                it.copy(isLoading = true)
            }
            val result = servicesApiRepository.getConnectedServices(
                token = userToken,
                clientId = clientId ?: ""
            )
            if (result is Resource.Success) {
                _servicesData.update {
                    it.copy(data = result.data!!)
                }
            }
            _servicesData.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun loadPersonInfo(): PersonModel? {
        val result = personalAccountRepository.getPersonData(
            token = userToken,
            personId = personId ?: "" //TODO: this shouldn't be null. rework later
        )
        if (result is Resource.Success) return result.data
        else throw UserRequestError(
            stringResource = result.stringResourceId!!,
            message = result.message
        )
    }

    private suspend fun loadClientInfo(): ClientModel? {
        return if (clientId != null) {
            val result = personalAccountRepository.getClientData(
                token = userToken,
                clientId = clientId
            )
            if (result is Resource.Success) result.data
            else throw UserRequestError(
                stringResource = result.stringResourceId!!,
                message = result.message
            )
        } else null
    }

    private suspend fun loadEmployeeInfo(): EmployeeModel? {
        return if (employeeId != null) {
            val result = personalAccountRepository.getEmployeeData(
                token = userToken,
                employeeId = employeeId
            )
            if (result is Resource.Success) result.data
            else throw UserRequestError(
                stringResource = result.stringResourceId!!,
                message = result.message
            )
        } else null
    }

}

private class UserRequestError(val stringResource: Int, message: String?): Exception(message)