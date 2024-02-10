package com.glazovnet.glazovnetapp.presentation.loginscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.domain.repository.UtilsApiRepository
import com.glazovnet.glazovnetapp.domain.usecase.AuthUseCase
import com.glazovnet.glazovnetapp.domain.utils.Resource
import com.glazovnet.glazovnetapp.presentation.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
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
    private val _saveAuthData = MutableStateFlow(false)
    val saveAuthData = _saveAuthData.asStateFlow()

    private val _loginState = MutableStateFlow(ScreenState<Unit>())
    val loginState = _loginState.asStateFlow()

    private val _messageString = Channel<Int>()
    val messageString = _messageString.receiveAsFlow()

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
    fun editIsSaveAuthData(isNeedToSave: Boolean) {
        _saveAuthData.update { isNeedToSave }
    }

    private fun loadIntroImage() {
        viewModelScope.launch {
            _introImageUrl.update { utilsApiRepository.getIntroImageUrl() }
            Log.i("TAG", "loadIntroImage: ${introImageUrl.value}")
        }
    }

    private fun loadAuthSettings() {
        with(localUserAuthDataRepository) {
            _username.update { getSavedUserLogin() ?: "" }
        }
    }

    fun login(isAsAdmin: Boolean, onLoginSuccessfully: () -> Unit) {
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
                    password = password.value,
                    asAdmin = isAsAdmin,
                    isRememberAuthData = saveAuthData.value
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
                        _messageString.send(result.stringResourceId!!)
                    }
                }
            } else {
                _loginState.update {
                    it.copy(
                        stringResourceId = R.string.login_screen_fields_are_empty_error
                    )
                }
                _messageString.send(R.string.login_screen_fields_are_empty_error)
            }
        }
    }
}