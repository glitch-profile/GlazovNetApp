package com.glazovnet.glazovnetapp.presentation.tariffs.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glazovnet.glazovnetapp.domain.models.tariffs.TariffModel
import com.glazovnet.glazovnetapp.domain.models.tariffs.TariffType
import com.glazovnet.glazovnetapp.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.domain.repository.TariffsApiRepository
import com.glazovnet.glazovnetapp.domain.utils.Resource
import com.glazovnet.glazovnetapp.presentation.ScreenState
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

    private val _tariffsState = MutableStateFlow(ScreenState<Map<TariffType, List<TariffModel>>>())
    val tariffsState = _tariffsState.asStateFlow()

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
}