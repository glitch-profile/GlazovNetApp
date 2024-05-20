package com.glazovnet.glazovnetapp.tariffs.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glazovnet.glazovnetapp.core.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.core.presentation.states.ScreenState
import com.glazovnet.glazovnetapp.personalaccount.domain.repository.UsersRepository
import com.glazovnet.glazovnetapp.tariffs.domain.model.ClientCurrentTariffData
import com.glazovnet.glazovnetapp.tariffs.domain.model.TariffModel
import com.glazovnet.glazovnetapp.tariffs.domain.repository.TariffsApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TariffsListViewModel @Inject constructor(
    private val tariffsApiRepository: TariffsApiRepository,
    private val usersRepository: UsersRepository,
    userAuthDataRepository: LocalUserAuthDataRepository
): ViewModel() {

    private val loginToken = userAuthDataRepository.getLoginToken() ?: ""
    private val clientId = userAuthDataRepository.getAssociatedClientId() ?: ""
    private val employeeId = userAuthDataRepository.getAssociatedEmployeeId()
    val isUserIsClient = userAuthDataRepository.getAssociatedClientId() != null

    private val _tariffsState = MutableStateFlow(ScreenState<Unit>())
    val tariffsState = _tariffsState.asStateFlow()
    private val _unlimitedTariffs = MutableStateFlow<List<TariffModel>>(emptyList())
    val unlimitedTariffs = _unlimitedTariffs.asStateFlow()
    private val _limitedTariffs = MutableStateFlow<List<TariffModel>>(emptyList())
    val limitedTariffs = _limitedTariffs.asStateFlow()

    private val _archiveTariffsState = MutableStateFlow(ScreenState<List<TariffModel>>())
    val archiveTariffsState = _archiveTariffsState.asStateFlow()

    private val _sheetData = MutableStateFlow<TariffDetailsModel?>(null)
    val sheetData = _sheetData.asStateFlow()

    private val _isArchiveSheetOpen = MutableStateFlow(false)
    val isArchiveSheetOpen = _isArchiveSheetOpen.asStateFlow()

    private val _isClientAsOrganization = MutableStateFlow(false)
    val isClientAsOrganization = _isClientAsOrganization.asStateFlow()

    private val _connectedTariffInfo = MutableStateFlow<ClientCurrentTariffData?>(null)
    val connectedTariffInfo =_connectedTariffInfo.asStateFlow()

    init {
        loadClientTariffData()
        loadIsClientAsOrganization()
    }

    fun loadActiveTariffs() {
        viewModelScope.launch {
            _tariffsState.update {
                it.copy(isLoading = true, stringResourceId = null, message = null)
            }
            val result = tariffsApiRepository.getActiveTariffs(
                token = loginToken,
                clientId = if (isUserIsClient) clientId else null,
                employeeId = employeeId
            )
            when (result) {
                is Resource.Success -> {
                    splitTariffs(result.data!!)
                    _tariffsState.update { it.copy(data = Unit) }
                }
                is Resource.Error -> {
                    _tariffsState.update {
                        it.copy(message = result.message, stringResourceId = result.stringResourceId)
                    }
                }
            }
            _tariffsState.update { it.copy(isLoading = false) }
        }
    }

    private fun splitTariffs(tariffs: List<TariffModel>) {
        val groupedTariffs = tariffs.groupBy { it.prepaidTraffic == null }
        _unlimitedTariffs.update { groupedTariffs[true] ?: emptyList() }
        _limitedTariffs.update { groupedTariffs[false] ?: emptyList() }
    }

    private fun loadClientTariffData() {
        viewModelScope.launch {
            if (isUserIsClient) {
                val result = usersRepository.getClientData(
                    token = loginToken,
                    clientId = clientId
                ) //TODO replace with specific request
                if (result is Resource.Success) {
                    val client = result.data!!
                    val currentTariffData = tariffsApiRepository.getTariffById(
                        tariffId = client.tariffId,
                        token = loginToken
                    ).data ?: return@launch
                    val pendingTariffData = if (client.pendingTariffId != null) {
                        tariffsApiRepository.getTariffById(
                            tariffId = client.pendingTariffId,
                            token = loginToken
                        ).data
                    } else null
                    _connectedTariffInfo.update {
                        ClientCurrentTariffData(
                            currentTariff = currentTariffData,
                            pendingTariff = pendingTariffData,
                            billingDate = client.debitDate
                        )
                    }
                }
            } else _connectedTariffInfo.update { null }
        }
    }

    private fun loadIsClientAsOrganization() {
        viewModelScope.launch {
            if (isUserIsClient) {
                val result = usersRepository.getClientData(
                    token = loginToken,
                    clientId = clientId
                )
                if (result is Resource.Success) {
                    _isClientAsOrganization.update { result.data!!.connectedOrganizationName != null }
                } else _isClientAsOrganization.update { false }
            }
        }
    }

    private fun loadArchiveTariffs() {
        viewModelScope.launch {
            _archiveTariffsState.update {
                it.copy(isLoading = true, stringResourceId = null, message = null)
            }
            val tariffs = tariffsApiRepository.getArchiveTariffs(
                token = loginToken,
                clientId = if (isUserIsClient) clientId else null,
                employeeId = employeeId
            )
            when (tariffs) {
                is Resource.Success -> {
                    _archiveTariffsState.update { it.copy(data = tariffs.data!!) }
                }
                is Resource.Error -> {
                    _archiveTariffsState.update {
                        it.copy(stringResourceId = tariffs.stringResourceId, message = tariffs.message)
                    }
                }
            }
            _archiveTariffsState.update { it.copy(isLoading = false) }
        }
    }

    fun connectTariff(tariffId: String?) {
        if (tariffId !== connectedTariffInfo.value?.pendingTariff?.id) {
            viewModelScope.launch {
                _tariffsState.update { it.copy(isUploading = true) }
                val result = tariffsApiRepository.changeTariff(
                    token = loginToken,
                    clientId = clientId,
                    newTariffId = tariffId
                )
                if (result is Resource.Success) {
                    val tariff = if (tariffId != null) getTariffById(tariffId) else null
                    _connectedTariffInfo.update { it?.copy(pendingTariff = tariff) }
                    closeSheet()
                } else {
                    _tariffsState.update {
                        it.copy(message = result.message, stringResourceId = result.stringResourceId)
                    }
                    closeSheet()
                }
                _tariffsState.update { it.copy(isUploading = false) }
            }
        }
    }

    fun showArchive() {
        loadArchiveTariffs()
        _isArchiveSheetOpen.update { true }
    }

    fun hideArchive() {
        _isArchiveSheetOpen.update { false }
    }

    private fun getTariffById(tariffId: String): TariffModel? {
        val tariffsList = unlimitedTariffs.value + limitedTariffs.value
        return tariffsList.find { it.id == tariffId }
    }

    fun showDetails(tariffId: String) {
        val connectedTariffsInfo = connectedTariffInfo.value
        if (connectedTariffsInfo != null) {
            when (tariffId) {
                connectedTariffsInfo.currentTariff.id -> {
                    _sheetData.update {
                        TariffDetailsModel(
                            tariff = connectedTariffsInfo.currentTariff,
                            isCurrentTariff = true,
                            isPendingTariff = false
                        )
                    }
                }
                connectedTariffsInfo.pendingTariff?.id -> {
                    _sheetData.update {
                        TariffDetailsModel(
                            tariff = connectedTariffsInfo.pendingTariff,
                            isCurrentTariff = false,
                            isPendingTariff = true
                        )
                    }
                }
                else -> {
                    _sheetData.update {
                        TariffDetailsModel(
                            tariff = getTariffById(tariffId)!!,
                            isCurrentTariff = false,
                            isPendingTariff = false
                        )
                    }
                }
            }
        } else {
            _sheetData.update {
                TariffDetailsModel(
                    tariff = getTariffById(tariffId)!!,
                    isCurrentTariff = false,
                    isPendingTariff = false
                )
            }
        }
    }

    fun closeSheet() {
        _sheetData.update { null }
    }
}