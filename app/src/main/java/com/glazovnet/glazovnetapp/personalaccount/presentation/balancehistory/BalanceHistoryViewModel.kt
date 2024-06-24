package com.glazovnet.glazovnetapp.personalaccount.presentation.balancehistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glazovnet.glazovnetapp.core.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.core.presentation.states.ScreenState
import com.glazovnet.glazovnetapp.personalaccount.domain.model.TransactionModel
import com.glazovnet.glazovnetapp.personalaccount.domain.repository.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class BalanceHistoryViewModel @Inject constructor(
    private val usersRepository: UsersRepository,
    userAuthDataRepository: LocalUserAuthDataRepository
): ViewModel() {

    private val loginToken = userAuthDataRepository.getLoginToken() ?: ""
    private val clientId = userAuthDataRepository.getAssociatedClientId() ?: ""

    private val _state = MutableStateFlow(ScreenState<Unit>())
    val state = _state.asStateFlow()
    private val _groupedTransactions = MutableStateFlow<Map<LocalDate, List<TransactionModel>>?>(null)
    val groupedTransactions = _groupedTransactions.asStateFlow()

    fun loadTransactions() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true, message = null, stringResourceId = null)
            }
            val result = usersRepository.loadBalanceHistory(
                token = loginToken,
                clientId = clientId
            )
            if (result is Resource.Success) {
                val transactions = result.data!!
                _groupedTransactions.update { transactions.groupBy { it.transactionTimestamp.toLocalDate() } }
                _state.update { it.copy(data = Unit) }
            } else {
                _state.update {
                    it.copy(message = result.message, stringResourceId = result.stringResourceId)
                }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }
}