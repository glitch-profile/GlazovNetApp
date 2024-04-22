package com.glazovnet.glazovnetapp.settings.core.presentation.mainscreen

import androidx.lifecycle.ViewModel
import com.glazovnet.glazovnetapp.core.domain.repository.LocalUserAuthDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    localUserAuthDataRepository: LocalUserAuthDataRepository
): ViewModel() {

    val isUserLoggedInAsGuest = localUserAuthDataRepository.getAssociatedPersonId() == "0"

}