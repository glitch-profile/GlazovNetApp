package com.glazovnet.glazovnetapp.presentation.supportscreen.requestdetails

import androidx.lifecycle.ViewModel
import com.glazovnet.glazovnetapp.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.domain.usecase.SupportRequestsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RequestDetailsViewModel @Inject constructor(
    private val requestsUseCase: SupportRequestsUseCase,
    private val userAuthDataRepository: LocalUserAuthDataRepository
): ViewModel() {
}