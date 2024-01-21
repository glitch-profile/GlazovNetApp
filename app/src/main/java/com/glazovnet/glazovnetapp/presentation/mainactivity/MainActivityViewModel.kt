package com.glazovnet.glazovnetapp.presentation.mainactivity

import androidx.lifecycle.ViewModel
import com.glazovnet.glazovnetapp.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.domain.usecase.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val userAuthDataRepository: LocalUserAuthDataRepository,
    private val authUseCase: AuthUseCase
): ViewModel() {
    fun getStartDestination(): String {
        val isUserSignedIn =  with(userAuthDataRepository) {
            getLoginToken() != null && getAssociatedUserId() != null
        }
        return if (isUserSignedIn) "home-screen" else "login-screen"
    }

    fun logout() {
        authUseCase.logout()
    }
}