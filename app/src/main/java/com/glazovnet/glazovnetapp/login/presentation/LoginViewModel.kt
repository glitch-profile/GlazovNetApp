package com.glazovnet.glazovnetapp.login.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.core.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.core.domain.repository.UtilsApiRepository
import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.core.presentation.states.MessageNotificationState
import com.glazovnet.glazovnetapp.core.presentation.states.ScreenState
import com.glazovnet.glazovnetapp.login.domain.usecases.AuthUseCase
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
class LoginViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val utilsApiRepository: UtilsApiRepository,
    private val localUserAuthDataRepository: LocalUserAuthDataRepository
): ViewModel() {

    private val _introImageUrl = MutableStateFlow("")
    val introImageUrl = _introImageUrl.asStateFlow()

    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()
    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _loginState = MutableStateFlow(ScreenState<Unit>())
    val loginState = _loginState.asStateFlow()

    private val _messageState = MutableStateFlow(MessageNotificationState())
    val messageState = _messageState.asStateFlow()
    private val messageScope = CoroutineScope(Dispatchers.Default + Job())

    init {
        loadIntroImage()
        loadAuthSettings()
    }

    fun editUsername(username: String) {
        _username.update { username }
    }
    fun editPassword(password: String) {
        _password.update { password }
    }

    private fun loadIntroImage() {
        viewModelScope.launch {
            _introImageUrl.update { utilsApiRepository.getIntroImageUrl() }
        }
    }

    private fun loadAuthSettings() {
        with(localUserAuthDataRepository) {
            _username.update { getSavedUserLogin() ?: "" }
        }
    }

    fun login(onLoginSuccessfully: () -> Unit) {
        viewModelScope.launch {
            if (username.value.isNotBlank() && password.value.isNotBlank()) {
                _loginState.update {
                    it.copy(
                        isLoading = true,
                        message = null,
                        stringResourceId = null
                    )
                }
                val result = authUseCase.login(
                    login = username.value,
                    password = password.value
                )
                when (result) {
                    is Resource.Success -> {
                        _loginState.update {
                            it.copy(
                                isLoading = false
                            )
                        }
                        onLoginSuccessfully.invoke()
                    }
                    is Resource.Error -> {
                        _loginState.update {
                            it.copy(
                                isLoading = false,
                                stringResourceId = result.stringResourceId,
                                message = result.message
                            )
                        }
                        showMessage(
                            messageRes = result.stringResourceId!!
                        )
                    }
                }
            } else {
                _loginState.update {
                    it.copy(
                        stringResourceId = R.string.login_screen_fields_are_empty_error_message
                    )
                }
                showMessage(
                    messageRes = R.string.login_screen_fields_are_empty_error_message
                )
            }
        }
    }

    private fun showMessage(titleRes: Int = R.string.login_screen_error_title, messageRes: Int) {
        messageScope.coroutineContext.cancelChildren()
        messageScope.launch {
            _messageState.update {
                MessageNotificationState(
                    enabled = true, titleResource = titleRes, additionTextResource = messageRes
                )
            }
            delay(3000L)
            _messageState.update { it.copy(enabled = false) }
        }
    }
}