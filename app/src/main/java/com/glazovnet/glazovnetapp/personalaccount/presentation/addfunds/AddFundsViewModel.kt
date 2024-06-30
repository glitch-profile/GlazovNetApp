package com.glazovnet.glazovnetapp.personalaccount.presentation.addfunds

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glazovnet.glazovnetapp.core.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.personalaccount.domain.repository.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddFundsViewModel @Inject constructor(
    private val usersRepository: UsersRepository,
    localUserAuthDataRepository: LocalUserAuthDataRepository
): ViewModel() {

    private val _state = MutableStateFlow<AddFundsScreenState>(AddFundsScreenState.EnteringInfo)
    val state = _state.asStateFlow()

    val loginToken = localUserAuthDataRepository.getLoginToken() ?: ""
    val clientId = localUserAuthDataRepository.getAssociatedClientId() ?: ""

    private val _amount = MutableStateFlow("")
    val amount = _amount.asStateFlow()
    private val _additionalNote = MutableStateFlow("")
    val additionalNote = _additionalNote.asStateFlow()

    fun makePayment() {
        viewModelScope.launch {
            updateScreenState(AddFundsScreenState.Loading)
            val note = additionalNote.value.ifBlank { null }
            val result = usersRepository.addFunds(
                token = loginToken,
                clientId = clientId,
                amount = amount.value.toFloat(),
                note = note
            )
            if (result is Resource.Success) {
                updateScreenState(AddFundsScreenState.Success)
            } else {
                updateScreenState(AddFundsScreenState.Error)
            }
        }
    }

    fun updateScreenState(screenState: AddFundsScreenState) {
        _state.update { screenState }
    }

    fun resetScreen() {
        _state.update { AddFundsScreenState.EnteringInfo }
        _amount.update { "" }
        _additionalNote.update { "" }
    }

    fun setAmount(value: String) {
        val formatted = value.filter { it.isDigit() || it == '.' || it == ',' }.replace(',', '.')
        val parts = formatted.split('.')
        val resultString = if (parts.size == 1) formatted
        else (parts[0]) + '.' + (parts[1].take(2))
        _amount.update { resultString }

    }
    fun setAdditionalNote(value: String) {
        _additionalNote.update { value.trimStart().take(75) }
    }

}