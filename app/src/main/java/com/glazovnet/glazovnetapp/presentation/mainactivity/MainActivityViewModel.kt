package com.glazovnet.glazovnetapp.presentation.mainactivity

import androidx.lifecycle.ViewModel
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.domain.usecase.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val userAuthDataRepository: LocalUserAuthDataRepository,
    private val authUseCase: AuthUseCase
): ViewModel() {

    private val notificationScope = CoroutineScope(Job()+Dispatchers.IO)
    private val _isShowingMessage = MutableStateFlow(false)
    val isShowingMessage = _isShowingMessage.asStateFlow()
    private val _messageResourceString = MutableStateFlow<Int>(R.string.api_response_unknown_error)
    val messageResourceString = _messageResourceString.asStateFlow()

    fun getStartDestination(): String {
        val isUserSignedIn =  with(userAuthDataRepository) {
            getLoginToken() != null && getAssociatedUserId() != null
        }
        return if (isUserSignedIn) "home-screen" else "login-screen"
    }

    fun logout() {
        authUseCase.logout()
    }

    fun showMessage(
        messageStringResource: Int
    ) {
        notificationScope.coroutineContext.cancelChildren()
        notificationScope.launch {
            _messageResourceString.update { messageStringResource }
            _isShowingMessage.update{ true }
            delay(3_000)
            _isShowingMessage.update { false }
        }
    }
}