package com.glazovnet.glazovnetapp.presentation.startscreen

import androidx.lifecycle.ViewModel
import com.glazovnet.glazovnetapp.domain.repository.LocalUserAuthDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StartScreenViewModel @Inject constructor(
    private val userAuthDataRepository: LocalUserAuthDataRepository
): ViewModel() {
    fun isUserSignedIn(): Boolean {
        return with(userAuthDataRepository) {
            getLoginToken() != null && getAssociatedUserId() != null
        }
    }
}