package com.glazovnet.glazovnetapp.tariffs.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glazovnet.glazovnetapp.core.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.core.presentation.states.ScreenState
import com.glazovnet.glazovnetapp.tariffs.domain.model.TariffModel
import com.glazovnet.glazovnetapp.tariffs.domain.model.TariffType
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
    private val userAuthDataRepository: LocalUserAuthDataRepository
): ViewModel() {

    val isUserIsClient = userAuthDataRepository.getAssociatedClientId() != null


    private val _tariffsState = MutableStateFlow(ScreenState<Map<TariffType, List<TariffModel>>>())
    val tariffsState = _tariffsState.asStateFlow()

    private val _sheetData = MutableStateFlow<TariffModel?>(null)
    val sheetData = _sheetData.asStateFlow()
    private val _isSheetOpen = MutableStateFlow(false)
    val isDetailsSheetOpen = _isSheetOpen.asStateFlow()

    fun loadTariffs() {
        viewModelScope.launch {
            _tariffsState.update {
                it.copy(isLoading = true, stringResourceId = null, message = null)
            }
            val result = tariffsApiRepository.getAllTariffs(
                token = userAuthDataRepository.getLoginToken() ?: ""
            )
            when (result) {
                is Resource.Success -> {
                    val tariffs = splitTariffs(result.data!!)
                    _tariffsState.update { it.copy(data = tariffs) }
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

    private fun splitTariffs(tariffs: List<TariffModel>): Map<TariffType, List<TariffModel>> {
        return tariffs.groupBy { it.category }
    }

    private fun getTariffById(tariffId: String): TariffModel? {
        val tariffsList = tariffsState.value.data?.values?.flatten()
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