package com.glazovnet.glazovnetapp.personalaccount.presentation

import androidx.lifecycle.ViewModel
import com.glazovnet.glazovnetapp.core.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.core.presentation.states.ScreenState
import com.glazovnet.glazovnetapp.personalaccount.domain.model.ClientModel
import com.glazovnet.glazovnetapp.personalaccount.domain.repository.PersonalAccountRepository
import com.glazovnet.glazovnetapp.tariffs.domain.model.TariffModel
import com.glazovnet.glazovnetapp.tariffs.domain.repository.TariffsApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val personalAccountRepository: PersonalAccountRepository,
    private val tariffsApiRepository: TariffsApiRepository,
     userAuthDataRepository: LocalUserAuthDataRepository,
): ViewModel() {

    private val _state = MutableStateFlow(ScreenState<ClientModel>())
    val state = _state.asStateFlow()

    private val _tariffData = MutableStateFlow(ScreenState<TariffModel>())
    val tariffData = _tariffData.asStateFlow()

    private val userToken = userAuthDataRepository.getLoginToken()

}