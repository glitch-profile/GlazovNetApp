package com.glazovnet.glazovnetapp.core.presentation.homescreen

import androidx.lifecycle.ViewModel
import com.glazovnet.glazovnetapp.core.domain.repository.LocalUserAuthDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    localUserAuthDataRepository: LocalUserAuthDataRepository
): ViewModel() {

    val isAdmin = localUserAuthDataRepository.getIsUserAsAdmin()

}