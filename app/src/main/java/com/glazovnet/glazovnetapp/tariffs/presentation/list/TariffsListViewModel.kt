package com.glazovnet.glazovnetapp.tariffs.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glazovnet.glazovnetapp.core.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.core.presentation.states.ScreenState
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
    userAuthDataRepository: LocalUserAuthDataRepository
): ViewModel() {

    private val loginToken = userAuthDataRepository.getLoginToken() ?: ""
    val isUserIsClient = userAuthDataRepository.getAssociatedClientId() != null
    private val isUserIsEmployee = userAuthDataRepository.getAssociatedEmployeeId() != null

    private val _tariffsState = MutableStateFlow(ScreenState<List<TariffModel>>())
    val tariffsState = _tariffsState.asStateFlow()
    private val _unlimitedTariffs = MutableStateFlow<List<TariffModel>>(emptyList())
    val unlimitedTariffs = _unlimitedTariffs.asStateFlow()
    private val _limitedTariffs = MutableStateFlow<List<TariffModel>>(emptyList())
    val limitedTariffs = _limitedTariffs.asStateFlow()

    private val _archiveTariffsState = MutableStateFlow(ScreenState<List<TariffModel>>())
    val archiveTariffsState = _archiveTariffsState.asStateFlow()

    private val _sheetData = MutableStateFlow<TariffModel?>(null)
    val sheetData = _sheetData.asStateFlow()
    private val _isSheetOpen = MutableStateFlow(false)
    val isDetailsSheetOpen = _isSheetOpen.asStateFlow()

    private val _isArchiveSheetOpen = MutableStateFlow(false)
    val isArchiveSheetOpen = _isArchiveSheetOpen.asStateFlow()

    fun loadActiveTariffs() {
        viewModelScope.launch {
            _tariffsState.update {
                it.copy(isLoading = true, stringResourceId = null, message = null)
            }
            val result = tariffsApiRepository.getActiveTariffs(
                token = loginToken,
                showOrganizationTariffs = isUserIsEmployee
            )
            when (result) {
                is Resource.Success -> {
                    splitTariffs(result.data!!)
                    _tariffsState.update { it.copy(data = result.data) }
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

    private fun loadArchiveTariffs() {
        viewModelScope.launch {
            _archiveTariffsState.update {
                it.copy(isLoading = true, stringResourceId = null, message = null)
            }
            val tariffs = tariffsApiRepository.getArchiveTariffs(
                token = loginToken,
                showOrganizationTariffs = isUserIsEmployee
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

    fun showArchive() {
        loadArchiveTariffs()
        _isArchiveSheetOpen.update { true }
    }

    fun hideArchive() {
        _isArchiveSheetOpen.update { false }
    }

    private fun getTariffById(tariffId: String): TariffModel? {
        val tariffsList = tariffsState.value.data
        return tariffsList?.find { it.id == tariffId }
    }

    fun showDetails(tariffId: String) {
        _sheetData.update {
            getTariffById(tariffId)
        }
        _isSheetOpen.update { true }
    }

    fun closeSheet() {
        _isSheetOpen.update { false }
    }
}